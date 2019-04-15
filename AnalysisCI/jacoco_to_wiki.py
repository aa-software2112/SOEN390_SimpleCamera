import os
import sys
import subprocess
from GitWikiFormatter import GitWikiFormatter
from GitWikiFormatter import GitApiHelper as Git
from TravisEnvirExtractor import TravisEnvirExtractor

jacoco_lines = []
retain_string = "total"
temp_line = ""

for line in sys.stdin:

        jacoco_lines.append(line.strip("\n"))


BASE_PATH_TO_WIKI = "SOEN390_SimpleCamera.wiki"
PATH_TO_WIKI = BASE_PATH_TO_WIKI + "/CI-Analysis.md"

# Assumes the Git folder already exists (otherwise, call Git.clone())

# Get all the travis environment variables
travis_output = TravisEnvirExtractor.get_travis_variables()

# Pull the old wiki
old_wiki = GitWikiFormatter().add_file(PATH_TO_WIKI)

# Setup the new wiki making sure to append the old
new_wiki = GitWikiFormatter().add_header(GitWikiFormatter.HEADER1, "CI Analysis")
new_wiki.add_header(GitWikiFormatter.HEADER2,
                    "Build #" + str(travis_output['TRAVIS_BUILD_NUMBER']) + ': ' +
                    "Jacoco Output" +  ': ' +
                    ("PR" if not (str(travis_output["TRAVIS_PULL_REQUEST"]) == "false") else "") + " "
                      + str(travis_output["TRAVIS_BRANCH"]))

# Header is in index 0, and rows are in rest (row 1 is just a delimiter row)
# If the format of the jacoco output changes, so may the line below
new_wiki.add_table(["package name", "instruction coverage %", "branch coverage %"], jacoco_lines[2:-3])
new_wiki.skip_line()
new_wiki.add_lines(jacoco_lines[-2:])
new_wiki.add_horiz_rule()
new_wiki.add_lines(old_wiki.get_markdown_after("CI Analysis"))
new_wiki.write_to_file(PATH_TO_WIKI)

# Enter the wiki directory to use git commands
os.chdir(BASE_PATH_TO_WIKI)

# Push changes to repo wiki
Git.commit()
Git.push(use_token=(os.environ.get("TRAVIS") is not None))
