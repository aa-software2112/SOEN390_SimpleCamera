import os
import sys
import subprocess
from GitWikiFormatter import GitWikiFormatter
from GitWikiFormatter import GitApiHelper as Git
from TravisEnvirExtractor import TravisEnvirExtractor

previous_line = ""
current_line = ""
last_task_executed = ""

error_keywords = ["fail", "error"]
success_keywords = ["success", "pass", "complete"]
ignore_keywords = ["> task", "> transform", "w:"]
TASK_STRING_INDEX = 0
information_keywords = ["executed"]

ignore_line = []
error_lines = []
success_lines = []
information_lines = []

if False:
    for line in sys.stdin:

        # Prevents infinite looping over the same input
        previous_line = current_line
        current_line = line.lower().strip("\n")

        if current_line == previous_line:
            continue

        # Keep track of the last task executed
        if ignore_keywords[TASK_STRING_INDEX] in current_line:
            last_task_executed = current_line

        # Check if line should be skipped - this is the filtering stage for every line of output
        [ignore_line.append(True) for keyword in ignore_keywords if keyword in current_line]


        if (len(ignore_line) > 0):
            ignore_line = []
            continue

        # Process the lines success or failure messages
        [error_lines.append(current_line) for keyword in error_keywords if keyword in current_line]
        [success_lines.append(current_line) for keyword in success_keywords if keyword in current_line]
        [information_lines.append(current_line) for keyword in information_keywords if keyword in current_line]


# Clone git wiki page
Git.clone()

BASE_PATH_TO_WIKI = "SOEN390_SimpleCamera.wiki"
PATH_TO_WIKI = BASE_PATH_TO_WIKI + "/CI-Analysis.md"

# Get all the travis environment variables
travis_output = TravisEnvirExtractor.get_travis_variables()

# Pull the old wiki
old_wiki = GitWikiFormatter().add_file(PATH_TO_WIKI)

# Setup the new wiki making sure to append the old
new_wiki = GitWikiFormatter().add_header(GitWikiFormatter.HEADER1, "CI Analysis")
new_wiki.add_header(GitWikiFormatter.HEADER2,
                    str(travis_output['TRAVIS_BUILD_NUMBER']) + ':' +
                    str(travis_output['TRAVIS_JOB_NUMBER']) + ':' +
                    (" PR" if not (str(travis_output["TRAVIS_PULL_REQUEST"]) == "false") else "") + " "
                      + str(travis_output["TRAVIS_BRANCH"]))
new_wiki.add_header(GitWikiFormatter.HEADER3, "Build Metadata")

for k, v in travis_output.items():
    new_wiki.add_key_value_pair(k, str(v))

new_wiki.add_header(GitWikiFormatter.HEADER3, "ERROR(S)").add_lines(error_lines)
new_wiki.add_header(GitWikiFormatter.HEADER3, "SUCCESS(ES)").add_lines(success_lines)
new_wiki.add_header(GitWikiFormatter.HEADER4, "INFO").add_lines(information_lines)
new_wiki.add_horiz_rule()
new_wiki.add_lines(old_wiki.get_markdown_after("CI Analysis"))
new_wiki.write_to_file(PATH_TO_WIKI)

# Enter the wiki directory to use git commands
os.chdir(BASE_PATH_TO_WIKI)

# Push changes to repo wiki
Git.commit()
Git.push(use_token=(os.environ.get("TRAVIS") is not None))
