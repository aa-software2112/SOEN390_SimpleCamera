import xml.etree.ElementTree as ET
import os
from sys import exit


total_instructions_missed = 0
total_instructions_covered = 0
total_branches_missed = 0
total_branches_covered = 0


def update_instruction_count(missed, covered):
    missed = float(missed)
    covered = float(covered)
    global total_instructions_missed, total_instructions_covered

    total_instructions_covered += covered
    total_instructions_missed += missed


def update_branch_count(missed, covered):
    missed = float(missed)
    covered = float(covered)
    global total_branches_missed, total_branches_covered

    total_branches_covered += covered
    total_branches_missed += missed

# Converts python3.* statements to python2.*
def print_alt(text, end=None):
    if end is None:
        print text
    else:
        print text,

def display_total():
    global total_instructions_missed, total_instructions_covered, total_branches_missed, total_branches_covered
    print_alt("Total Instruction Coverage {}%".format(
        round(100 * (total_instructions_covered / (total_instructions_covered + total_instructions_missed))), 2))
    print_alt("Total Branch Coverage {}%".format(
        round(100 * (total_branches_covered / (total_branches_covered + total_branches_missed))), 2))


def display(missed_count, covered_count, extra_text=""):
    missed_count = float(missed_count)
    covered_count = float(covered_count)

    print_alt("{: <20}".format(round((float(covered_count) / float(covered_count + missed_count)) * 100, 2)), end="")


def display_package_instruction(missed_count, covered_count):
    display(missed_count, covered_count, "instructions covered")


def display_package_branch(missed_count, covered_count):
    display(missed_count, covered_count, "branches covered")


def display_na_branch():
    print_alt("{: <15}".format("n/a"), end="")


def display_package(package_name):
    print_alt("{: <50}".format(package_name), end="")


def display_header():
    print_alt("{: <50}{: <20}{: <15}".format("package", "instr. cov. (%)", "branch cov. (%)"))
    print_alt("{: <50}{: <20}{: <15}".format("-" * 49, "-" * 19, "-" * 14))


REL_PATH_TO_JACOCO_XML = "app/build/reports/jacoco/jacocoTestReport/" + "jacocoTestReport.xml"

if not os.path.isfile(REL_PATH_TO_JACOCO_XML):
    print_alt("Could not find " + REL_PATH_TO_JACOCO_XML + "... Exiting")
    exit(1)

tree = ET.parse(REL_PATH_TO_JACOCO_XML)
root = tree.getroot()
package = None
missed = 0
covered = 0

display_header()
branch_metric_exists = False

for child in root:
    if (child.tag == "package"):
        display_package(child.attrib.get("name"))
        branch_metric_exists = False
        for c in child:
            if (c.tag == "counter"):
                missed = c.attrib.get("missed")
                covered = c.attrib.get("covered")
                if (c.attrib.get("type") == "INSTRUCTION"):
                    display_package_instruction(missed, covered)
                    update_instruction_count(missed, covered)
                elif (c.attrib.get("type") == "BRANCH"):
                    display_package_branch(missed, covered)
                    update_branch_count(missed, covered)
                    branch_metric_exists = True
        if branch_metric_exists == False:
            display_na_branch()
        print_alt("")

print_alt("")
display_total()

exit(0)
