package com.mikepenz.aboutlibraries.entity;

public class Library implements Comparable<Library> {

    private String definedName = "";

    private boolean internal = false;

    private String author = "";
    private String authorWebsite = "";
    private String libraryName = "";
    private String libraryDescription = "";
    private String libraryVersion = "";
    private String libraryWebsite = "";
    private License license;

    private boolean isOpenSource = true;
    private String repositoryLink = "";

    private String classPath = "";

    public Library() {

    }

    public Library(String author, String libraryName, String libraryDescription) {
        this.author = author;
        this.libraryName = libraryName;
        this.libraryDescription = libraryDescription;
    }

    public Library(String author, String libraryName, String libraryDescription, String libraryVersion) {
        this.author = author;
        this.libraryName = libraryName;
        this.libraryDescription = libraryDescription;
        this.libraryVersion = libraryVersion;
    }

    public String getDefinedName() {
        return definedName;
    }

    public void setDefinedName(String definedName) {
        this.definedName = definedName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorWebsite() {
        return authorWebsite;
    }

    public void setAuthorWebsite(String authorWebsite) {
        this.authorWebsite = authorWebsite;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryDescription() {
        return libraryDescription;
    }

    public void setLibraryDescription(String libraryDescription) {
        this.libraryDescription = libraryDescription;
    }

    public String getLibraryVersion() {
        return libraryVersion;
    }

    public void setLibraryVersion(String libraryVersion) {
        this.libraryVersion = libraryVersion;
    }

    public String getLibraryWebsite() {
        return libraryWebsite;
    }

    public void setLibraryWebsite(String libraryWebsite) {
        this.libraryWebsite = libraryWebsite;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public boolean isOpenSource() {
        return isOpenSource;
    }

    public void setOpenSource(boolean isOpenSource) {
        this.isOpenSource = isOpenSource;
    }

    public String getRepositoryLink() {
        return repositoryLink;
    }

    public void setRepositoryLink(String repositoryLink) {
        this.repositoryLink = repositoryLink;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    @Override
    public int compareTo(Library another) {
        return getLibraryName().compareToIgnoreCase(another.getLibraryName());
    }
}
