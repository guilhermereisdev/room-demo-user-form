package com.guilhermereisdev.roomdemo

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
            if (subscriberToUpdateOrDelete.name.isNotEmpty()
                && subscriberToUpdateOrDelete.email.isNotEmpty()
            )
                update(subscriberToUpdateOrDelete)
            else
                statusMessage.value = Event("Os campos de nome e e-mail precisam estar preenchidos")
        } else {
            val name = inputName.value ?: ""
            val email = inputEmail.value ?: ""
            if (name.isNotEmpty() && email.isNotEmpty()) {
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            } else
                statusMessage.value = Event("Os campos de nome e e-mail precisam estar preenchidos")
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) delete(subscriberToUpdateOrDelete) else deleteAll()
    }

    fun insert(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(subscriber)
        withContext(Dispatchers.Main) {
            statusMessage.value = Event("Usuário inserido com sucesso")
        }
    }

    fun update(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(subscriber)
        withContext(Dispatchers.Main) {
            inputName.value = ""
            inputEmail.value = ""
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Salvar"
            clearAllOrDeleteButtonText.value = "Excluir todos"
            statusMessage.value = Event("Usuário atualizado com sucesso")
        }
    }

    fun delete(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(subscriber)
        withContext(Dispatchers.Main) {
            inputName.value = ""
            inputEmail.value = ""
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Salvar"
            clearAllOrDeleteButtonText.value = "Excluir todos"
            statusMessage.value = Event("Usuário excluído com sucesso")
        }
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
        withContext(Dispatchers.Main) {
            statusMessage.value = Event("Todos os usuários excluídos com sucesso")
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
