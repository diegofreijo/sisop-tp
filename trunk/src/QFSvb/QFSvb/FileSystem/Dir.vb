Namespace QFSvb.FileSystem

    Public Class Dir
        Inherits FSElement

        Private dPadre As Dir

        Public Property padre() As Dir
            Get
                Return Me.dPadre
            End Get
            Set(ByVal value As Dir)
                Me.dPadre = value
            End Set
        End Property
        ''' <summary>
        ''' Constructor por defecto
        ''' </summary>
        Sub New()
            MyBase.New()
        End Sub

        Sub New(ByVal idFSElement As Integer)
            MyBase.New(idFSElement)
        End Sub


    End Class

End Namespace


