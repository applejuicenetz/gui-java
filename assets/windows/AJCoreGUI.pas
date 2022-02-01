program AJCoreGUI;
uses ShellAPI, SysUtils;

{$R AJCoreGUI.rc}

var     verzeichnis, argumente, arg: string;
        i: integer;

begin
verzeichnis := ExtractFilePath(ParamStr(0));
if (fileexists(verzeichnis + '\AJCoreGUI.jar')=true) then
begin
        argumente := '-jar AJCoreGUI.jar';
        i := 1;

        for i := 1 to paramCount() do
            while ParamStr(i)<>'' do begin
                arg:=ParamStr(i);
                if copy(arg,0,8)='ajfsp://' then arg:='-link='+arg;
                argumente:=argumente+' "'+arg+'"';
                i:=i+1;
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
