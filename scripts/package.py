#!/usr/bin/env python3
"""Prepare portable ZIP and native Java 25 installers (run after mvn clean package)."""
import argparse
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile
from urllib.parse import unquote

from windows_installer import add_legacy_guard

ROOT = Path(__file__).resolve().parent.parent
TARGET = ROOT / 'target'
INPUT = TARGET / 'distribution'
NAME = 'AJCoreGUI'
MAIN = 'de.applejuicenet.client.AppleJuiceClient'


def validate_classpath(directory):
    with zipfile.ZipFile(directory / f'{NAME}.jar') as archive:
        manifest = archive.read('META-INF/MANIFEST.MF').decode('utf-8')
    manifest = manifest.replace('\r\n', '\n').replace('\n ', '')
    main_section = manifest.split('\n\n', 1)[0]
    classpath = next((line[len('Class-Path: '):] for line in main_section.splitlines()
                      if line.startswith('Class-Path: ')), None)
    if not classpath:
        raise RuntimeError(f'{NAME}.jar has no dependency Class-Path')
    missing = [entry for entry in classpath.split() if not (directory / unquote(entry)).is_file()]
    if missing:
        raise RuntimeError(f'Missing Class-Path files: {", ".join(missing)}')

def prepare():
    jar = ROOT / 'modules/AJCoreGUI/target/AJCoreGUI.jar'
    if not jar.is_file():
        raise RuntimeError('First run mvn clean package with JDK 25')
    if INPUT.exists():
        shutil.rmtree(INPUT)
    shutil.copytree(ROOT / 'resources', INPUT)
    shutil.copy2(jar, INPUT / jar.name)
    validate_classpath(INPUT)
    (INPUT / 'README.txt').write_text(
        'appleJuice JavaGUI\nRequires Java 25 for this portable ZIP.\n'
        'Start: java -jar AJCoreGUI.jar\n'
        'Native installers and Flatpak bundles include Java 25.\n', encoding='utf-8')
    with zipfile.ZipFile(TARGET / f'{NAME}.zip', 'w', zipfile.ZIP_DEFLATED) as archive:
        for file in sorted(INPUT.rglob('*')):
            if file.is_file():
                archive.write(file, file.relative_to(INPUT).as_posix())


def windows_resources(jdk_home, directory):
    # Use matching JDK's WiX template; jpackage converts it to WiX 4/5 as needed.
    with zipfile.ZipFile(jdk_home / 'jmods/jdk.jpackage.jmod') as jmod:
        template = jmod.read('classes/jdk/jpackage/internal/resources/main.wxs').decode('utf-8')
    if template.count('<Feature Id="DefaultFeature"') != 1 or template.count('</Feature>') != 1:
        raise RuntimeError('Unsupported jpackage WiX template; check protocol registration')
    template = template.replace('</Feature>', '<ComponentRef Id="AjfspProtocol"/>\n    </Feature>')
    fragment = '''
  <Fragment>
    <DirectoryRef Id="INSTALLDIR">
      <Component Id="AjfspProtocol" Guid="*">
        <RegistryKey Root="HKLM" Key="Software\\Classes\\ajfsp">
          <RegistryValue Type="string" Value="URL:appleJuice File Sharing Protocol"/>
          <RegistryValue Name="URL Protocol" Type="string" Value="" KeyPath="yes"/>
          <RegistryKey Key="DefaultIcon">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot;,0"/>
          </RegistryKey>
          <RegistryKey Key="shell\\open\\command">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot; &quot;%1&quot;"/>
          </RegistryKey>
        </RegistryKey>
        <RegistryKey Root="HKLM" Key="Software\\Classes\\web+ajfsp">
          <RegistryValue Type="string" Value="URL:appleJuice File Sharing Protocol"/>
          <RegistryValue Name="URL Protocol" Type="string" Value=""/>
          <RegistryKey Key="DefaultIcon">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot;,0"/>
          </RegistryKey>
          <RegistryKey Key="shell\\open\\command">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot; &quot;%1&quot;"/>
          </RegistryKey>
        </RegistryKey>
        <RegistryKey Root="HKLM" Key="Software\\Classes\\appleJuiceNETZ.URI.JavaGUI">
          <RegistryValue Type="string" Value="URL:ajfsp"/>
          <RegistryValue Name="URL Protocol" Type="string" Value=""/>
          <RegistryKey Key="DefaultIcon">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot;,0"/>
          </RegistryKey>
          <RegistryKey Key="shell\\open\\command">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot; &quot;%1&quot;"/>
          </RegistryKey>
        </RegistryKey>
        <RegistryKey Root="HKLM" Key="Software\\Classes\\appleJuiceNETZ.EXT.JavaGUI">
          <RegistryValue Type="string" Value="appleJuice Linkliste"/>
          <RegistryKey Key="DefaultIcon">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot;,0"/>
          </RegistryKey>
          <RegistryKey Key="shell\\open\\command">
            <RegistryValue Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot; &quot;%1&quot;"/>
          </RegistryKey>
        </RegistryKey>
        <RegistryKey Root="HKLM" Key="Software\\appleJuiceNETZ\\JavaGUI\\Capabilities">
          <RegistryValue Name="ApplicationName" Type="string" Value="JavaGUI"/>
          <RegistryValue Name="ApplicationIcon" Type="string" Value="&quot;[INSTALLDIR]appleJuice JavaGUI.exe&quot;,0"/>
          <RegistryValue Name="ApplicationDescription" Type="string" Value="appleJuice GUI"/>
          <RegistryKey Key="URLAssociations">
            <RegistryValue Name="ajfsp" Type="string" Value="appleJuiceNETZ.URI.JavaGUI"/>
            <RegistryValue Name="web+ajfsp" Type="string" Value="appleJuiceNETZ.URI.JavaGUI"/>
          </RegistryKey>
          <RegistryKey Key="FileAssociations">
            <RegistryValue Name=".ajl" Type="string" Value="appleJuiceNETZ.EXT.JavaGUI"/>
          </RegistryKey>
        </RegistryKey>
        <RegistryValue Root="HKLM" Key="Software\\RegisteredApplications" Name="JavaGUI" Type="string"
                       Value="Software\\appleJuiceNETZ\\JavaGUI\\Capabilities"/>
      </Component>
    </DirectoryRef>
  </Fragment>
'''
    template = template.replace('</Wix>', fragment + '</Wix>')
    template = add_legacy_guard(template)
    (directory / 'main.wxs').write_text(template, encoding='utf-8')


def mac_resources(jdk_home, directory, display_version=None):
    with zipfile.ZipFile(jdk_home / 'jmods/jdk.jpackage.jmod') as jmod:
        template = jmod.read('classes/jdk/jpackage/internal/resources/Info-lite.plist.template').decode('utf-8')
    url_types = """
  <key>CFBundleURLTypes</key>
  <array><dict>
    <key>CFBundleURLName</key><string>appleJuice Links</string>
    <key>CFBundleURLSchemes</key><array><string>ajfsp</string><string>web+ajfsp</string></array>
  </dict></array>
"""
    if template.count('</plist>') != 1:
        raise RuntimeError('Unsupported jpackage macOS plist template')
    index = template.rfind('</dict>')
    template = template[:index] + url_types + template[index:]
    if display_version is not None:
        template = template.replace('DEPLOY_BUNDLE_SHORT_VERSION', display_version)
    (directory / 'Info.plist').write_text(template, encoding='utf-8')


def native(args):
    expected = {'macos': 'darwin', 'windows': 'win32', 'linux': 'linux'}[args.platform]
    if not sys.platform.startswith(expected):
        raise RuntimeError('jpackage requires a native build host for each OS')
    jpackage = shutil.which('jpackage')
    if not jpackage or not subprocess.check_output([jpackage, '--version'], text=True).strip().startswith('25'):
        raise RuntimeError('jpackage from JDK 25 required')
    java = shutil.which('java')
    settings = subprocess.run([java, '-XshowSettings:properties', '-version'],
                              capture_output=True, text=True, check=True)
    architecture = re.search(r'os.arch\s*=\s*(\S+)', settings.stderr)
    aliases = {'x86_64': 'amd64', 'amd64': 'amd64', 'arm64': 'aarch64', 'aarch64': 'aarch64'}
    if not architecture or aliases.get(architecture.group(1)) != args.arch:
        raise RuntimeError(f'JDK architecture does not match requested {args.arch}')
    prepare()
    output = TARGET / 'jpackage-output'
    resources = TARGET / 'jpackage-resources'
    for directory in [output, resources]:
        if directory.exists():
            shutil.rmtree(directory)
        directory.mkdir(parents=True)
    version = args.version or ET.parse(ROOT / 'pom.xml').getroot().findtext('version')
    if not re.fullmatch(r'\d+\.\d+\.\d+', version):
        raise RuntimeError(f'Installer version must have three numeric parts: {version}')
    # CFBundleVersion requires a positive first component; GUI version remains unchanged.
    installer_version = '1' + version[1:] if args.platform == 'macos' and version.startswith('0.') else version
    associations = resources / 'ajl.properties'
    associations.write_text('extension=ajl\nmime-type=application/x-ajl\ndescription=appleJuice Link List\n', encoding='utf-8')
    options = [
        '--name', 'appleJuice JavaGUI' if args.platform == 'windows' else NAME,
        '--app-version', installer_version, '--vendor', 'appleJuiceNETZ',
        '--description', 'appleJuice JavaGUI', '--input', str(INPUT),
        '--main-jar', f'{NAME}.jar', '--main-class', MAIN,
        '--add-modules', 'java.desktop,java.management,java.naming,java.net.http,java.sql,java.xml,jdk.crypto.ec,jdk.unsupported,jdk.localedata',
        '--java-options', '--enable-native-access=ALL-UNNAMED',
        '--dest', str(output),
    ]
    if args.type != 'app-image':
        options += ['--file-associations', str(associations)]
    if args.platform == 'windows':
        java_home = Path(os.environ['JAVA_HOME'])
        windows_resources(java_home, resources)
        options += ['--icon', str(ROOT / 'assets/windows/AJCoreGUI.ico'),
                    '--resource-dir', str(resources),
                    '--copyright', 'appleJuiceNETZ',
                    '--about-url', 'https://applejuicenetz.github.io',
                    '--win-help-url', 'https://applejuicenetz.github.io/faq/',
                    '--install-dir', 'appleJuiceNETZ/JavaGUI',
                    '--win-dir-chooser',
                    '--win-menu', '--win-menu-group', 'appleJuiceNETZ',
                    '--win-shortcut',
                    '--win-upgrade-uuid', 'c15e8267-840b-4c4c-8a24-28555bc08c27']
    elif args.platform == 'macos':
        mac_resources(Path(os.environ['JAVA_HOME']), resources, version)
        options += ['--icon', str(ROOT / 'assets/mac/AJCoreGUI.icns'),
                    '--mac-package-identifier', MAIN, '--resource-dir', str(resources),
                    '--java-options', '-Dapple.awt.application.appearance=system',
                    '--java-options', f'-Djpackage.app-version={version}']
    else:
        options += ['--icon', str(ROOT / 'assets/linux/AJCoreGUI.png')]
    subprocess.run([jpackage, '--type', args.type, *options], check=True)
    if args.type == 'app-image':
        return
    packages = list(output.glob(f'*.{args.type}'))
    if len(packages) != 1:
        raise RuntimeError(f'Expected exactly one installer, got {packages}')
    shutil.copy2(packages[0], TARGET / f'{NAME}-{args.platform}-{args.arch}.{args.type}')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    sub = parser.add_subparsers(dest='command', required=True)
    sub.add_parser('prepare')
    p = sub.add_parser('native')
    p.add_argument('--platform', choices=['macos', 'windows', 'linux'], required=True)
    p.add_argument('--arch', choices=['amd64', 'aarch64'], required=True)
    p.add_argument('--type', choices=['dmg', 'exe', 'app-image'], required=True)
    p.add_argument('--version')
    args = parser.parse_args()
    os.chdir(ROOT)
    if args.command == 'prepare':
        prepare()
    else:
        native(args)


if __name__ == '__main__':
    main()
