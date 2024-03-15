package com.example.recruitica

import android.os.Parcel
import android.os.Parcelable

// Class for Candidate Data
data class CandidateData(
    var userID: Int = 0,
    var name: String = "",
    var gender: String = "",
    var company: String = "",
    var location: String = "",
    var bio: String = "",
    var photo: String = "",
    var username: String = "",
    var password: String = "",

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt()?:0,
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",

    )

    // Creating a Parcelable data to pass in different components
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userID)
        parcel.writeString(name)
        parcel.writeString(gender)
        parcel.writeString(company)
        parcel.writeString(location)
        parcel.writeString(bio)
        parcel.writeString(photo)
        parcel.writeString(username)
        parcel.writeString(password)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CandidateData> {
        override fun createFromParcel(parcel: Parcel): CandidateData {
            return CandidateData(parcel)
        }

        override fun newArray(size: Int): Array<CandidateData?> {
            return arrayOfNulls(size)
        }
    }
}
