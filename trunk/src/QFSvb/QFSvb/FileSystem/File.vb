Namespace QFSvb.FileSystem

    Public Class File
        Inherits FSElement

        Private sRuta As String

        Sub New(ByVal idFSElement As Integer)
            MyBase.New(idFSElement)
        End Sub

        Public Property ruta()
            Get
                ruta = Me.sRuta
            End Get
            Set(ByVal sValue)
                Me.sRuta = sValue
            End Set
        End Property

    End Class

End Namespace
