"""Package an existing JavaGUI payload with a verified 32-bit Java 21 runtime."""
import argparse
from pathlib import Path
import shutil
import tarfile
import tempfile
import zipfile


def package_archive(payload, runtime, destination, platform, arch):
    destination.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as temporary:
        root = Path(temporary) / 'AJCoreGUI'
        shutil.copytree(payload, root)
        shutil.copytree(runtime, root / 'jre')
        if platform == 'windows':
            (root / 'ajgui.cmd').write_text(
                '@echo off\r\ncd /d "%~dp0"\r\n'
                '"%~dp0jre\\bin\\java.exe" --enable-preview '
                '--enable-native-access=ALL-UNNAMED -Xmx384m '
                '-Djava.net.preferIPv4Stack=true -jar "%~dp0AJCoreGUI.jar" %*\r\n',
                encoding='utf-8')
            output = destination / f'AJCoreGUI-windows-{arch}.zip'
            with zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as archive:
                for file in sorted(root.rglob('*')):
                    if file.is_file():
                        archive.write(file, file.relative_to(root.parent))
        else:
            launcher = root / 'ajgui'
            launcher.write_text(
                '#!/bin/sh\nset -eu\n'
                'ROOT=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)\n'
                'cd "$ROOT"\nexec "$ROOT/jre/bin/java" --enable-preview '
                '--enable-native-access=ALL-UNNAMED -Xmx384m '
                '-Djava.net.preferIPv4Stack=true -Dsun.java2d.xrender=false '
                '-jar "$ROOT/AJCoreGUI.jar" "$@"\n', encoding='utf-8')
            launcher.chmod(0o755)
            output = destination / f'AJCoreGUI-linux-{arch}.tar.gz'
            with tarfile.open(output, 'w:gz') as archive:
                archive.add(root, arcname=root.name)
    return output


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--payload', type=Path, required=True)
    parser.add_argument('--runtime', type=Path, required=True)
    parser.add_argument('--destination', type=Path, default=Path('target'))
    parser.add_argument('--platform', choices=['linux', 'windows'], required=True)
    parser.add_argument('--arch', choices=['x86', 'armhf'], required=True)
    args = parser.parse_args()
    print(package_archive(args.payload, args.runtime, args.destination, args.platform, args.arch))
