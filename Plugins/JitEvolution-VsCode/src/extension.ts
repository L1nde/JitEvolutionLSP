import * as vscode from 'vscode';
import * as path from 'path';

import { LanguageClientOptions } from 'vscode-languageclient';
import { ServerOptions, LanguageClient } from "vscode-languageclient/node";
import { config } from 'process';

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
		const config = vscode.workspace.getConfiguration('jitevolution');

		const args: string[] = ['-cp', classPath, "-Xdebug", "-Xrunjdwp:server=y,transport=dt_socket,address=8000,suspend=n,quiet=y"];
		
		// Set the server options
		// -- java execution path
		// -- argument to be pass when executing the java command
		let serverOptions: ServerOptions = {
			command: excecutable,
			args: [...args, main, config.get<string>("api")!, config!.get<string>("api-key")!, config!.get<string>("url")!, ".java"],
			options: {}
		};
		// Options to control the language client
		let clientOptions: LanguageClientOptions = {
			// Register the server for plain text documents
			documentSelector: [{ scheme: 'file', language: 'jitevolution' }]
		};

		// Create the language client and start the client.
		const client = new LanguageClient('JitEvolution', 'JitEvolution', serverOptions, clientOptions);

		// Disposables to remove on deactivation.
		context.subscriptions.push(client.start());

		context.subscriptions.push(vscode.commands.registerCommand("jit-evolution.send", () => {
			client.onReady().then(() => {
				client.sendRequest("workspace/executeCommand", { command: "sendToAnalyzer"});
			});
		}));

		context.subscriptions.push(vscode.commands.registerCommand("jit-evolution.open", () => {
			client.onReady().then(() => {
				client.sendRequest("workspace/executeCommand", { command: "openVisualization" });
			});
		}));

	}
}

// this method is called when your extension is deactivated
export function deactivate() {
	console.log('JitEvolution is deactivated');
}
