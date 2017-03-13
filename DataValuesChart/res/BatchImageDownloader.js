var fsObject = WScript.CreateObject("Scripting.FileSystemObject");
var fileObj = fsObject.GetFile(WScript.ScriptFullName);
var currentFolder = fsObject.GetParentFolderName(fileObj);

var destFolderName = fsObject.GetFile(WScript.arguments(0));
destFolderName = fsObject.GetFileName(destFolderName).split(".")[0].toLowerCase();

var folder = currentFolder + "\\" + destFolderName + "\\";

if(!fsObject.FolderExists(folder)) {
	fsObject.CreateFolder(folder);
}

var urls = [];
var urlFile = fsObject.OpenTextFile(WScript.arguments(0), 1);

var i = 0;
while(!urlFile.AtEndOfStream) {
	urls[i] = urlFile.ReadLine();
	i++;
}

WScript.echo("Downloading files to folder: " + folder);

for(i = 0; i < urls.length; i++) {
	var xmlHttp = WScript.CreateObject("MSXML2.XMLHTTP");
	xmlHttp.open("GET", urls[i], false);
	xmlHttp.send();
	var responseBody = xmlHttp.responseBody;

	var adodbStream = WScript.CreateObject("ADODB.Stream");
	adodbStream.type = 1;
	adodbStream.Open();
	adodbStream.Write(responseBody);

	var saveFileName = urls[i];
	saveFileName = saveFileName.replace(saveFileName.replace(/[^\/\\&\?]+\.\w{3,4}(?=([\?&].*$|$))/g, ""), "");
	saveFileName = folder + saveFileName.replace(/(%[A-Fa-f0-9][A-Fa-f0-9])/g, "");
	//WScript.echo(saveFileName);

	adodbStream.SaveToFile(saveFileName);
	adodbStream.Close();
}