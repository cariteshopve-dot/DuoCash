package agency.lacosta.duocash.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import agency.lacosta.duocash.AppContainer
import agency.lacosta.duocash.DuoCashApp

val CreationExtras.container: AppContainer
    get() = (this[APPLICATION_KEY] as DuoCashApp).container
