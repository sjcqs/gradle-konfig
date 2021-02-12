package fr.sjcqs.transpiler

import fr.sjcqs.ast.Token

interface FileTranspiler {
    fun transpile(packageName: String = "", root: Token.Root): FileWriter
}