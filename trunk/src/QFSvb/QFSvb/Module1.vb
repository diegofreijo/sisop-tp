Imports QFSvb.QFSvb.FileSystem

Namespace QFSvb

    Module Module1

        Sub Main()

            Dim elem As New File(1)

            elem.name = "archivo.txt"

            System.Console.WriteLine(elem.idFSElement)
            System.Console.WriteLine(elem.name)

        End Sub

    End Module

End Namespace
