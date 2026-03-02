#!/usr/bin/python
import sys


def main(argv):
    writeFile(argv[0], argv[1])

# not very elegant, but does the job


def writeFile(name, version):
    f = open("library_" + name.lower() + "_strings.xml", 'w')
    f.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
    f.write("<resources> \n")
    f.write("	<string name=\"define_int_" + name + "\"></string>\n")
    f.write("	<!-- Author section -->\n")
    f.write("	<string name=\"library_" + name + "_author\"></string>\n")
    f.write("	<string name=\"library_" + name + "_authorWebsite\"></string>\n")
    f.write("	<!-- Library section -->\n")
    f.write("	<string name=\"library_" + name + "_libraryName\">" + name + "</string>\n")
    f.write("	<string name=\"library_" + name + "_libraryDescription\"></string>\n")
    f.write("	<string name=\"library_" + name + "_libraryWebsite\"></string>\n")
    f.write("	<string name=\"library_" + name + "_libraryVersion\">" + version + "</string>\n")
    f.write("	<!-- OpenSource section -->\n")
    f.write("	<string name=\"library_" + name + "_isOpenSource\">true</string>\n")
    f.write("	<string name=\"library_" + name + "_repositoryLink\"></string>\n")
    f.write("	<!-- License section -->\n")
    f.write("	<string name=\"library_" + name + "_licenseId\"></string>\n")
    f.write("</resources> \n")
    f.close
    pass


if __name__ == "__main__":
    if len(sys.argv) == 3:
        main(sys.argv[1:])
    else:
        print("too few arguments. Need library name and version")
