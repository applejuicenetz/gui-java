program AJCoreGUI;
uses ShellAPI, SysUtils;

{$R project.rc}

var     verzeichnis, argumente, arg: string;
        i: integer;

{$R *.res}

begin
verzeichnis := ExtractFilePath(ParamStr(0));
if (fileexists(verzeichnis + '\AJCoreGUI.jar')=true) then
begin
        argumente := '-jar AJCoreGUI.jar';
        i := 1;

        for i := 1 to paramCount() do
        begin
             argumente := argumente +' ' + paramStr(i);
        end;

        if (fileexists(verzeichnis + '\Java\bin\javaw.exe')=true) then
          begin
             ShellExecute(0, 'open' ,PChar(verzeichnis + '\Java\bin\javaw.exe'), PChar(argumente), PChar(verzeichnis), 1);
          end
        else
          begin
             ShellExecute(0, 'open' ,PChar('javaw.exe'), PChar(argumente), PChar(verzeichnis), 1);
          end;
        end;
end.
