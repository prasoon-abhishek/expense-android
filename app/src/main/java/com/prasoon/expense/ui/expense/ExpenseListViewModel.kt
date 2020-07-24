package com.prasoon.expense.ui.expense

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prasoon.expense.data.local.ExpenseRepository
import com.prasoon.expense.model.ExpenseItem
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ExpenseListViewModel(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private var categoryId: Long = 0

    private val _showToast = MutableLiveData<String>()
    val showToast = _showToast

    private val _showKeyboard = MutableLiveData<Boolean>()
    val showKeyboard = _showKeyboard

    private val _expenseItemList = MutableLiveData<ArrayList<ExpenseItem>>()
    val expenseItemList = _expenseItemList
    fun onFragmentLoaded(categoryId: Long) {
        this.categoryId = categoryId
        updateExpenseList()
    }

    private val _showAlert = MutableLiveData<Boolean>()
    val showAlert = _showAlert
    fun onAddExpensePressed() {
        _showAlert.value = true
    }

    fun cancelAlertPressed() {
        _showKeyboard.value = false
        _showAlert.value = false
    }

    private val _showAddExpenseError = MutableLiveData<String>()
    val showAddExpenseError = _showAddExpenseError
    fun confirmExpensePressed(amount: String, note: String) {
        if (amount.isEmpty()) {
            _showAddExpenseError.value = "Amount Can't Be Empty"
            return
        }
        try {
            amount.toDouble().let {

                viewModelScope.launch {
                    expenseRepository.saveExpense(
                        ExpenseItem(
                            System.currentTimeMillis(),
                            it,
                            note,
                            categoryId
                        )
                    )
                    viewModelScope.launch {
                        expenseRepository.updateTotalExpense(
                            categoryId,
                            expenseRepository.fetchTotalExpense(categoryId) + it
                        )
                    }
                    _showToast.value = "expense added successfully"
                    _showKeyboard.value = false
                    _showAlert.value = false
                    updateExpenseList()
                }
            }
        } catch (exception: NumberFormatException) {
            _showAddExpenseError.value = "Amount Must Be Numeric only"
        }
    }

    private fun updateExpenseList() {
        viewModelScope.launch {
            expenseRepository.fetchExpense(categoryId)?.let {
                _expenseItemList.value = it as ArrayList<ExpenseItem>
            }
        }
    }

}