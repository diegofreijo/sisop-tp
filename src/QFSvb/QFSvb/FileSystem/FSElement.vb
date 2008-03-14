Namespace QFSvb.FileSystem

    Public MustInherit Class FSElement

        Private iId As Integer
        Private sName As String

        'Constructor por defecto
        Sub New()

        End Sub

        Sub New(ByVal idFSElement As Integer)
            Me.iId = idFSElement
        End Sub

        ''' <summary>
        ''' 
        ''' </summary>
        ''' <value></value>
        ''' <returns></returns>
        ''' <remarks></remarks>
        Public ReadOnly Property idFSElement()
            Get
                idFSElement = Me.iId
            End Get
        End Property

        Public Property name()
            Get
                name = Me.sName
            End Get
            Set(ByVal sValue)
                Me.sName = sValue
            End Set
        End Property

    End Class

End Namespace
