package com.example.elderlauncher.repo

import com.example.elderlauncher.dao.ContactDao
import com.example.elderlauncher.model.Contact

class ContactRepository(private val dao: ContactDao) {
    suspend fun getTopContacts() = dao.getTopContacts()
    suspend fun insert(contact: Contact) = dao.insert(contact)
    suspend fun update(contact: Contact) = dao.update(contact)
    suspend fun delete(contact: Contact) = dao.delete(contact)
}
