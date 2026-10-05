import tempfile
import unittest
from pathlib import Path
from archive import package_archive

class ArchiveTest(unittest.TestCase):
    def test_linux_launcher_uses_bundled_java_and_preview(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            payload = root / 'payload'
            runtime = root / 'runtime'
            payload.mkdir()
            (runtime / 'bin').mkdir(parents=True)
            (payload / 'AJCoreGUI.jar').write_bytes(b'test payload')
            (runtime / 'bin/java').write_text('runtime')
            output = package_archive(payload, runtime, root / 'out', 'linux', 'x86')
            import tarfile
            with tarfile.open(output) as archive:
                launcher = archive.extractfile('AJCoreGUI/ajgui').read().decode()
                self.assertIn('--enable-preview', launcher)
                self.assertIn('jre/bin/java', launcher)
                self.assertIn('"$@"', launcher)
                self.assertTrue(archive.getmember('AJCoreGUI/ajgui').mode & 0o111)

if __name__ == '__main__':
    unittest.main()
