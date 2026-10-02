import tempfile
import unittest
from pathlib import Path
from zipfile import ZipFile

from package import validate_classpath


class ClasspathTest(unittest.TestCase):
    def make_distribution(self, directory, classpath, libraries):
        with ZipFile(directory / 'AJCoreGUI.jar', 'w') as archive:
            archive.writestr('META-INF/MANIFEST.MF',
                             f'Manifest-Version: 1.0\r\n{classpath}\r\n\r\n')
        for library in libraries:
            path = directory / library
            path.parent.mkdir(parents=True, exist_ok=True)
            path.touch()

    def test_snapshot_and_folded_classpath_match_packaged_files(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            self.make_distribution(directory,
                                   'Class-Path: ./lib/libtray-java-0.1.0-SNAP\r\n SHOT.jar ./lib/helper.jar',
                                   ['lib/libtray-java-0.1.0-SNAPSHOT.jar', 'lib/helper.jar'])
            validate_classpath(directory)

    def test_timestamped_snapshot_missing_from_package_fails(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            self.make_distribution(directory,
                                   'Class-Path: ./lib/libtray-java-0.1.0-20261002.134539-1.jar',
                                   ['lib/libtray-java-0.1.0-SNAPSHOT.jar'])
            with self.assertRaisesRegex(RuntimeError, 'Missing Class-Path files:.*20261002'):
                validate_classpath(directory)

    def test_url_encoded_filename(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            self.make_distribution(directory, 'Class-Path: lib/helper%20library.jar',
                                   ['lib/helper library.jar'])
            validate_classpath(directory)

    def test_missing_classpath_fails(self):
        with tempfile.TemporaryDirectory() as temporary:
            directory = Path(temporary)
            self.make_distribution(directory, 'Main-Class: example.Main', [])
            with self.assertRaisesRegex(RuntimeError, 'no dependency Class-Path'):
                validate_classpath(directory)


if __name__ == '__main__':
    unittest.main()
