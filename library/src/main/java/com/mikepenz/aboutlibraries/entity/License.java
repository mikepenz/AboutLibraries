package com.mikepenz.aboutlibraries.entity;

/**
 * Created by mikepenz on 08.06.14.
 */
public class License {
    private String definedName;

    private String licenseName;
    private String licenseWebsite;
    private String licenseShortDescription;
    private String licenseDescription;

    public License() {
    }

    public License(String licenseName, String licenseWebsite, String licenseShortDescription, String licenseDescription) {
        this.licenseName = licenseName;
        this.licenseWebsite = licenseWebsite;
        this.licenseShortDescription = licenseShortDescription;
        this.licenseDescription = licenseDescription;
    }

    public License copy() {
        return new License(this.licenseName, this.licenseWebsite, this.licenseShortDescription, this.licenseDescription);
    }

    public String getDefinedName() {
        return definedName;
    }

    public void setDefinedName(String definedName) {
        this.definedName = definedName;
    }

    public String getLicenseName() {
        return licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getLicenseWebsite() {
        return licenseWebsite;
    }

    public void setLicenseWebsite(String licenseWebsite) {
        this.licenseWebsite = licenseWebsite;
    }

    public String getLicenseShortDescription() {
        return licenseShortDescription;
    }

    public void setLicenseShortDescription(String licenseShortDescription) {
        this.licenseShortDescription = licenseShortDescription;
    }

    public String getLicenseDescription() {
        return licenseDescription;
    }

    public void setLicenseDescription(String licenseDescription) {
        this.licenseDescription = licenseDescription;
    }
}
