package com.guilhermereisdev.roomdemo

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermereisdev.roomdemo.db.Subscriber
import com.guilhermereisdev.roomdemo.db.SubscriberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriberViewModel(
    private val repository: SubscriberRepository
) : ViewModel() {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()

    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Salvar"
        clearAllOrDeleteButtonText.value = "Excluir todos"
    }

    fun saveOrUpdate() {
        if (isUpdateOrDelete) {
            subscriberToUpdateOrDelete.name = inputName.value.toString()
            subscriberToUpdateOrDelete.email = inputEmail.value.toString()
            if (subscriberToUpdateOrDelete.name.isEmpty()
                && subscriberToUpdateOrDelete.email.isEmpty()
            )
                statusMessage.value = Event("Os campos de nome e e-mail precisam estar preenchidos")
            else if (!Patterns.EMAIL_ADDRESS.matcher(subscriberToUpdateOrDelete.email).matches())
                statusMessage.value = Event("Insira um e-mail válido")
            else
                update(subscriberToUpdateOrDelete)
        } else {
            val name = inputName.value ?: ""
            val email = inputEmail.value ?: ""
            if (name.isEmpty() && email.isEmpty()) {
                statusMessage.value = Event("Os campos de nome e e-mail precisam estar preenchidos")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                statusMessage.value = Event("Insira um e-mail válido")
            else {
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) delete(subscriberToUpdateOrDelete) else deleteAll()
    }

    private fun insert(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val newRowId = repository.insert(subscriber)
        withContext(Dispatchers.Main) {
            if (newRowId > -1)
                statusMessage.value = Event("Usuário inserido com sucesso. Id: $newRowId")
            else
                statusMessage.value = Event("Um erro ocorreu")
        }
    }

    private fun update(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRows = repository.update(subscriber)
        withContext(Dispatchers.Main) {
            if (numberOfRows > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Salvar"
                clearAllOrDeleteButtonText.value = "Excluir todos"
                statusMessage.value = Event("Usuário atualizado com sucesso")
            } else
                statusMessage.value = Event("Um erro ocorreu")
        }
    }

    private fun delete(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRowsDeleted = repository.delete(subscriber)
        withContext(Dispatchers.Main) {
            if (numberOfRowsDeleted > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Salvar"
                clearAllOrDeleteButtonText.value = "Excluir todos"
                statusMessage.value = Event("Usuário excluído com sucesso")
            } else
                statusMessage.value = Event("Um erro ocorreu")
        }
    }

    private fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRowsDeleted = repository.deleteAll()
        withContext(Dispatchers.Main) {
            if (numberOfRowsDeleted > 0)
                statusMessage.value = Event("$numberOfRowsDeleted usuários excluídos com sucesso")
            else
                statusMessage.value = Event("Um erro ocorreu")
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Atualizar"
        clearAllOrDeleteButtonText.value = "Excluir"
    }

}
