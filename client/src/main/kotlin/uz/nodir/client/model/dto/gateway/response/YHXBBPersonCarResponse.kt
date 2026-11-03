package uz.nodir.client.model.dto.gateway.response

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import uz.repay.common.utils.serializer.DateDefaultDeSerializer
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class YHXBBPersonCarResponse(
    @JsonProperty("pResult")
    val result: Int? = null,

    @JsonProperty("pComment")
    val comment: String? = null,

    @JsonProperty("pPinpp")
    val personCode: String? = null,

    @JsonProperty("pOwner")
    val owner: String? = null,

    @JsonProperty("pOwnerType")
    val ownerType: Int? = null,

    @JsonProperty("pOrganizationInn")
    val organizationInn: String? = null,

    @JsonProperty("Vehicle")
    val vehicles: List<Vehicle>? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Vehicle(
    @JsonProperty("pVehicleId")
    val vehicleId: Int? = null,

    @JsonProperty("pTexpassportSery")
    val registrationCertificateSeries: String? = null,

    @JsonProperty("pTexpassportNumber")
    val registrationCertificateNumber: String? = null,

    @JsonProperty("pPlateNumber")
    val plateNumber: String? = null,

    @JsonProperty("pModel")
    val model: String? = null,

    @JsonProperty("pVehicleColor")
    val vehicleColor: String? = null,

    @JsonProperty("pRegistrationDate")
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val registrationDate: LocalDate? = null,

    @JsonProperty("pDivision")
    val division: String? = null,

    @JsonProperty("pYear")
    val year: Int? = null,

    @JsonProperty("pVehicleType")
    val vehicleType: Int? = null,

    @JsonProperty("pBodyTypeName")
    val bodyTypeName: String? = null,

    @JsonProperty("pKuzov")
    val body: String? = null,

    @JsonProperty("pShassi")
    val chassis: String? = null,

    @JsonProperty("pFullWeight")
    val fullWeight: Int? = null,

    @JsonProperty("pEmptyWeight")
    val emptyWeight: Int? = null,

    @JsonProperty("pMotor")
    val motor: String? = null,

    @JsonProperty("pFuelType")
    val fuelType: Int? = null,

    @JsonProperty("pSeats")
    val seats: Int? = null,

    @JsonProperty("pStands")
    val stands: Int? = null,

    @JsonProperty("pComments")
    val comments: String?,

    @JsonProperty("pPower")
    val power: Int? = null,

    @JsonProperty("pDateSchetSpravka")
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val accountReferenceDate: LocalDate? = null,

    @JsonProperty("pTuningPermit")
    val tuningPermit: String? = null,

    @JsonProperty("pTuningGivenDate")
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val tuningGivenDate: LocalDate? = null,

    @JsonProperty("pTuningIssueDate")
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val tuningIssueDate: LocalDate? = null,

    @JsonProperty("prevPnfl")
    val previousPersonCode: String? = null,

    @JsonProperty("prevOwner")
    val previousOwner: String? = null,

    @JsonProperty("prevOwnerType")
    val previousOwnerType: Int? = null,

    @JsonProperty("prevPlateNumber")
    val previousPlateNumber: String? = null,

    @JsonProperty("prevTexpasportSery")
    val previousRegistrationCertificateSeries: String? = null,

    @JsonProperty("prevTexpasportNumber")
    val previousRegistrationCertificateNumber: String? = null,

    @JsonProperty("State")
    val state: String? = null,

    @JsonProperty("NextInspectionDate")
    @JsonFormat(pattern = "dd.MM.yyyy")
    @JsonDeserialize(using = DateDefaultDeSerializer::class)
    val nextInspectionDate: LocalDate? = null,

    @JsonProperty("specNotes")
    val specNotes: String? = null,

    @JsonProperty("address")
    val address: Address? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Address(

    @JsonProperty("regionId")
    val regionId: Int? = null,

    @JsonProperty("cityId")
    val cityId: Int? = null,

    @JsonProperty("address")
    val address: String? = null,
)
