Imports QFSvb.QFSvb.FileSystem

Namespace QFSvb.HdDriver

    ''' <summary>
    ''' Representa el driver por el cual el FS se comunica con el disco rigido 
    ''' </summary>
    ''' <remarks></remarks>
    Public Class HdDriver

        Private Class FSPhysicalElement

            Private sNombreArchivoFisico As String
            Private sRutaArchivoFisico As String


            Public Property nombreArchivoFisico() As String
                Get
                    Return Me.sNombreArchivoFisico
                End Get
                Set(ByVal value As String)
                    Me.sNombreArchivoFisico = value
                End Set
            End Property

            Public Property rutaArchivoFisico() As String
                Get
                    Return Me.sRutaArchivoFisico
                End Get
                Set(ByVal value As String)
                    Me.sRutaArchivoFisico = value
                End Set
            End Property



        End Class

        Private arrListaArchivos(100) As FSPhysicalElement






    End Class

End Namespace