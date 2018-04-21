package ru.alexunder.ttracker.ui

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import tornadofx.*
import javax.json.JsonObject


/**
 * The employee domain model, implementing JsonModel so it can be fetched
 * via the REST API
 */
class Employee : JsonModel {
    val nameProperty = SimpleStringProperty()
    var name by nameProperty

    val salaryProperty = SimpleIntegerProperty()
    var salary by salaryProperty

    val genderProperty = SimpleObjectProperty<Gender>()
    var gender by genderProperty

    override fun updateModel(json: JsonObject) {
        with (json) {
            name = getString("name")
            salary = getInt("salary")
            gender = Gender.valueOf(getString("gender"))
        }
    }
}

enum class Gender { Male, Female }

/**
 * Container for the list of employees as well as a search function called by the filter
 * view whenever it should update the employee list.
 */
class EmployeeContext : Controller() {
    val api: Rest by inject()
    val query: EmployeeQuery by inject()
    val employees = SimpleListProperty<Employee>()

    fun search() {
        runAsync {
            FXCollections.observableArrayList(Employee().apply {
                name = "Edvin Syse"
                gender = Gender.Male
                salary = 200_000
            })
            //api.post("employees/query", query).list().toModel<Employee>()
        } ui {
            employees.value = it
        }
    }
}

/**
 * Query object used to define the query sent to the server
 */
class EmployeeQuery : ViewModel(), JsonModel {
    val genderProperty = SimpleObjectProperty<Gender>(Gender.Female)
    var gender by genderProperty

    val salaryMinProperty = SimpleIntegerProperty(50_000)
    var salaryMin by salaryMinProperty

    val salaryMaxProperty = SimpleIntegerProperty(250_000)
    var salaryMax by salaryMaxProperty

    val salaryDescription = stringBinding(salaryMinProperty, salaryMaxProperty) {
        "$$salaryMin - $$salaryMax"
    }

    override fun toJSON(json: JsonBuilder) {
        with(json) {
            add("gender", gender.toString())
            add("salaryMin", salaryMin)
            add("salaryMax", salaryMax)
        }
    }
}

/**
 * The search/filter UI
 */
class EmployeeFilterView : View() {
    val query: EmployeeQuery by inject()
    val context: EmployeeContext by inject()

    override val root = form {
        fieldset("Employee Filter") {
            field("Gender") {
                combobox(query.genderProperty, Gender.values().toList())
            }
            field("Salary Range") {
                vbox {
                    alignment = Pos.CENTER
/*
                    add(RangeSlider().apply {
                        max = 500_000.0
                        lowValueProperty().bindBidirectional(query.salaryMinProperty)
                        highValueProperty().bindBidirectional(query.salaryMaxProperty)
                    })
*/
                    label(query.salaryDescription)
                }
            }
            button("Search").action {
                context.search()
            }
        }
    }
}

/**
 * The UI that shows the search results
 */
class EmployeeTableView : View() {
    val context: EmployeeContext by inject()

    override val root = borderpane {
        center {
            tableview(context.employees) {
                column("Name", Employee::nameProperty)
                column("Gender", Employee::genderProperty)
                column("Salary", Employee::salaryProperty)
            }
        }
    }
}

/**
 * A sample view that ties the filter UI and result UI together
 */
class MainView : View("Employee App") {
    override val root = hbox {
        add(EmployeeFilterView::class)
        add(EmployeeTableView::class)
    }
}