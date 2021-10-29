import * as vscode from 'vscode';
import * as path from 'path';

import { LanguageClientOptions } from 'vscode-languageclient';
import { ServerOptions, LanguageClient } from "vscode-languageclient/node";

const main: string = 'ee.linde.launcher.ServerLauncher';

export function activate(context: vscode.ExtensionContext) {
	console.log('JitEvolution is activated');

	const { JAVA_HOME } = process.env;

	if (JAVA_HOME) {
		// Java execution path.
		let excecutable: string = path.join(JAVA_HOME, 'bin', 'java');

		// path to the launcher.jar
		let classPath = path.join(__dirname, '..', '..', '..', 'JitEvolution-Server', 'target', 'JitEvolution-Server.jar');
		//Todo: This has debugging parameters
		const apiUrl = vscode.workspace.getConfiguration('jitevolution').get<string>("api");
		const args: string[] = ['-cp', classPath, "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n,quiet=y"];
		
		// Set the server options
		// -- java execution path
		// -- argument to be pass when executing the java command
		let serverOptions: ServerOptions = {
			command: excecutable,
			args: [...args, main, apiUrl!],
			options: {}
		};
		// Options to control the language client
		let clientOptions: LanguageClientOptions = {
			// Register the server for plain text documents
			documentSelector: [{ scheme: 'file', language: 'jitevolution' }]
		};

		// Create the language client and start the client.
		let disposable = new LanguageClient('JitEvolution', 'JitEvolution', serverOptions, clientOptions).start();

		// Disposables to remove on deactivation.
		context.subscriptions.push(disposable);
	}
}

// this method is called when your extension is deactivated
export function deactivate() {
	console.log('JitEvolution is deactivated');
}
