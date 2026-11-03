package uz.nodir.client.model.dto.yhxbb.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import uz.nodir.client.model.dto.gateway.response.Address
import uz.nodir.client.model.dto.gateway.response.Vehicle
import uz.nodir.client.model.dto.gateway.response.YHXBBPersonCarResponse
import java.time.LocalDate


@JsonIgnoreProperties(ignoreUnknown = true)
data class YHXBBPersonCarResponseDTO(
    @JsonProperty("pResult")
    val result: Int? = null,

    @JsonProperty("comment")
    val comment: String? = null,

    @JsonProperty("person_code")
    val personCode: String? = null,

    @JsonProperty("owner")
    val owner: String? = null,

    @JsonProperty("ownerType")
    val ownerType: Int? = null,

    @JsonProperty("organizationInn")
    val organizationInn: String? = null,

    @JsonProperty("vehicles")
    val vehicles: List<VehicleDTO>? = null,
) {
    companion object {
        fun from(response: YHXBBPersonCarResponse?): YHXBBPersonCarResponseDTO {
            return YHXBBPersonCarResponseDTO(
                result = response?.result,
                comment = response?.comment,
                personCode = response?.personCode,
                owner = response?.owner,
                ownerType = response?.ownerType,
                organizationInn = response?.organizationInn,
                vehicles = response?.vehicles?.map(VehicleDTO.Companion::from)
            )
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class VehicleDTO(
    @JsonProperty("vehicleId")
    val vehicleId: Int? = null,

    @JsonProperty("registrationCertificateSeries")
    val registrationCertificateSeries: String? = null,

    @JsonProperty("registrationCertificateNumber")
    val registrationCertificateNumber: String? = null,

    @JsonProperty("plateNumber")
    val plateNumber: String? = null,

    @JsonProperty("model")
    val model: String? = null,

    @JsonProperty("vehicleColor")
    val vehicleColor: String? = null,

    @JsonProperty("registrationDate")
    val registrationDate: LocalDate? = null,

    @JsonProperty("division")
    val division: String? = null,

    @JsonProperty("year")
    val year: Int? = null,

    @JsonProperty("vehicleType")
    val vehicleType: Int? = null,

    @JsonProperty("bodyTypeName")
    val bodyTypeName: String? = null,

    @JsonProperty("body")
    val body: String? = null,

    @JsonProperty("chassis")
    val chassis: String? = null,

    @JsonProperty("fullWeight")
    val fullWeight: Int? = null,

    @JsonProperty("emptyWeight")
    val emptyWeight: Int? = null,

    @JsonProperty("motor")
    val motor: String? = null,

    @JsonProperty("fuelType")
    val fuelType: Int? = null,

    @JsonProperty("seats")
    val seats: Int? = null,

    @JsonProperty("stands")
    val stands: Int? = null,

    @JsonProperty("comments")
    val comments: String?,

    @JsonProperty("power")
    val power: Int? = null,

    @JsonProperty("accountReferenceDate")
    val accountReferenceDate: LocalDate? = null,

    @JsonProperty("tuningPermit")
    val tuningPermit: String? = null,

    @JsonProperty("tuningGivenDate")
    val tuningGivenDate: LocalDate? = null,

    @JsonProperty("tuningIssueDate")
    val tuningIssueDate: LocalDate? = null,

    @JsonProperty("previousPersonCode")
    val previousPersonCode: String? = null,

    @JsonProperty("previousOwner")
    val previousOwner: String? = null,

    @JsonProperty("previousOwnerType")
    val previousOwnerType: Int? = null,

    @JsonProperty("previousPlateNumber")
    val previousPlateNumber: String? = null,

    @JsonProperty("previousRegistrationCertificateSeries")
    val previousRegistrationCertificateSeries: String? = null,

    @JsonProperty("previousRegistrationCertificateNumber")
    val previousRegistrationCertificateNumber: String? = null,

    @JsonProperty("state")
    val state: String? = null,

    @JsonProperty("nextInspectionDate")
    val nextInspectionDate: LocalDate? = null,

    @JsonProperty("specNotes")
    val specNotes: String? = null,

    @JsonProperty("address")
    val address: AddressDTO? = null,
) {
    companion object {
        fun from(vehicle: Vehicle?): VehicleDTO {
            return VehicleDTO(
                vehicleId = vehicle?.vehicleId,
                registrationCertificateSeries = vehicle?.registrationCertificateSeries,
                registrationCertificateNumber = vehicle?.registrationCertificateNumber,
                plateNumber = vehicle?.plateNumber,
                model = vehicle?.model,
                vehicleColor = vehicle?.vehicleColor,
                registrationDate = vehicle?.registrationDate,
                division = vehicle?.division,
                year = vehicle?.year,
                vehicleType = vehicle?.vehicleType,
                bodyTypeName = vehicle?.bodyTypeName,
                body = vehicle?.body,
                chassis = vehicle?.chassis,
                fullWeight = vehicle?.fullWeight,
                emptyWeight = vehicle?.emptyWeight,
                motor = vehicle?.motor,
                fuelType = vehicle?.fuelType,
                seats = vehicle?.seats,
                stands = vehicle?.stands,
                comments = vehicle?.comments,
                power = vehicle?.power,
                accountReferenceDate = vehicle?.accountReferenceDate,
                tuningPermit = vehicle?.tuningPermit,
                tuningGivenDate = vehicle?.tuningGivenDate,
                tuningIssueDate = vehicle?.tuningIssueDate,
                previousPersonCode = vehicle?.previousPersonCode,
                previousOwner = vehicle?.previousOwner,
                previousOwnerType = vehicle?.previousOwnerType,
                previousPlateNumber = vehicle?.previousPlateNumber,
                previousRegistrationCertificateSeries = vehicle?.previousRegistrationCertificateSeries,
                previousRegistrationCertificateNumber = vehicle?.previousRegistrationCertificateNumber,
                state = vehicle?.state,
                nextInspectionDate = vehicle?.nextInspectionDate,
                specNotes = vehicle?.specNotes,
                address = AddressDTO.from(vehicle?.address),
            )
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class AddressDTO(

    @JsonProperty("regionId")
    val regionId: Int? = null,

    @JsonProperty("cityId")
    val cityId: Int? = null,

    @JsonProperty("address")
    val address: String? = null,
) {
    companion object {
        fun from(address: Address?): AddressDTO {
            return AddressDTO(
                regionId = address?.regionId,
                cityId = address?.cityId,
                address = address?.address
            )
        }
    }
}
