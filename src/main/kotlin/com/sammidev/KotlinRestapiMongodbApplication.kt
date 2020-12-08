package com.sammidev

import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@SpringBootApplication
class KotlinRestapiMongodbApplication

fun main(args: Array<String>) {
	runApplication<KotlinRestapiMongodbApplication>(*args)
}

@Document
data class Patient (
		@Id
		val id: ObjectId = ObjectId.get(),
		val name: String,
		val age: Int,
		val desease: String,
		val description: String,
		val createdDate: LocalDateTime = LocalDateTime.now(),
		val updatedDate: LocalDateTime = LocalDateTime.now()
)

interface PatientRepository : MongoRepository<Patient, String> {
	fun findOneById(id: ObjectId) : Patient
	override fun deleteAll()
}

class PatientRequest(
		val name: String,
		val age: Int,
		val desease: String,
		val description: String,
)

@RestController
@RequestMapping("/api/v1/patients")
class PatientController (
		@Autowired
		private val repository: PatientRepository
){

	@GetMapping
	fun getAllPatient(): ResponseEntity<List<Patient>> {
		val patient = repository.findAll()
		return ResponseEntity.ok(patient)
	}

	@GetMapping("/{id}")
	fun getOnePatient(@PathVariable("id") id: String) : ResponseEntity<Patient> {
		val patient = repository.findOneById(ObjectId(id))
		return ResponseEntity.ok(patient)
	}

	@PostMapping
	fun createPatient(@RequestBody request: PatientRequest) : ResponseEntity<Patient>{
		val patient = repository.save(
				Patient(
						name = request.name,
						age = request.age,
						desease = request.desease,
						description = request.description,
				))
		return ResponseEntity(patient, HttpStatus.CREATED)
	}

	@PutMapping("/{id}")
	fun updatePatient(@RequestBody request: PatientRequest, @PathVariable("id") id: String) : ResponseEntity<Patient> {
		val patient = repository.findOneById(ObjectId(id))
		val updatedPatient = repository.save(
				Patient(
						id = patient.id,
						name = request.name,
						age = request.age,
						desease = request.desease,
						description = request.description,
						createdDate = patient.createdDate,
						updatedDate = LocalDateTime.now()
				))
		return ResponseEntity.ok(updatedPatient)
	}

	@DeleteMapping("/{id}")
	fun deletePatient(@PathVariable("id") id: String): ResponseEntity<Unit> {
		repository.deleteById(id)
		return ResponseEntity.noContent().build()
	}
}