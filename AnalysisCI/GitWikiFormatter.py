import os
import subprocess

from TravisEnvirExtractor import TravisEnvirExtractor

class GitApiHelper():

    @staticmethod
    def config(email, username):
        subprocess.Popen(['git', 'config', '--global', 'user.email', email]).communicate()

    @staticmethod
    def push(use_token=False):
        if not use_token:
            subprocess.Popen(['git', 'push', '--quiet', 'https://github.com/aa-software2112/SOEN390_SimpleCamera.wiki.git/', 'refs/heads/master:refs/heads/master']).communicate()
        else:
            subprocess.Popen(['git', 'push', '--quiet', 'https://' + str(os.environ.get("GH_TOKEN")) + '@github.com/aa-software2112/SOEN390_SimpleCamera.wiki.git/' ,'refs/heads/master:refs/heads/master']).communicate()

    @staticmethod
    def commit():
        subprocess.Popen(['git', 'commit', '-am', '\"travis\"']).communicate()

    @staticmethod
    def clone():
        subprocess.Popen(['git', 'clone', 'https://github.com/aa-software2112/SOEN390_SimpleCamera.wiki.git']).communicate()

class GitWikiFormatter():

    HEADER1 = 1
    HEADER2 = 2
    HEADER3 = 3
    HEADER4 = 4
    HEADER5 = 5
    HEADER6 = 6


    def __init__(self):
        self.lines = []

    def add_file(self, filename):

        f = open(filename, 'r')

        for line in f.readlines():
            self.add_line(line)

        return self


    def add_line(self, string):
        string = string.strip(" ")
        self.lines.append((string + "  ") if "\n" in string else (string + "  ") + "\n")
        return self


    def add_lines(self, lines):
        for line in lines:
            self.add_line(line)
        return self

    def add_header(self, header_level, string):
        self.add_line(header_level*"#" + " " + string)
        return self

    def add_key_value_pair(self, key, value):
        self.add_line(self.surr_bold(key) + ": " + self.surr_italics(value))
        return self

    def add_link(self, text, url):
        self.add_line(self.surr_sqbrack(text) + self.surr_brackets(url))
        return self

    def surr(self, text, surr_char):
        return surr_char + text + surr_char

    def surr_sqbrack(self, string):
        return "[" + string + "]"

    def surr_italics(self, string):
        return self.surr(string, "_")

    def surr_bold(self, string):
        return self.surr(string, "**")

    def surr_brackets(self, string):
        return "(" + string + ")"

    def add_horiz_rule(self):
        self.add_line("***")
        return self

    def add_underline(self):
        self.add_line("======")
        return self

    def get_markdown_after(self, delimiter):
        tokens = "".join(self.lines).split(delimiter)
        if len(tokens) > 1:
            return [line for line in tokens[1].split("\n") if line != '']
        return ""

    def write_to_file(self, filename):

        f = open(filename, 'w')
        f.writelines(self.lines)

    def __add__(self, other):
        return GitWikiFormatter().add_lines(self.lines + other.lines)

    def __str__(self):

        for l in self.lines:
            print(l,"")

        return ""

if __name__ == "__main__":
    PATH_TO_WIKI = "../SOEN390_SimpleCamera.wiki/CI-Analysis.md"

    travis_output = TravisEnvirExtractor.get_travis_variables()

    old_wiki = GitWikiFormatter().add_file(PATH_TO_WIKI)

    # Setup the new wiki
    new_wiki = GitWikiFormatter().add_header(GitWikiFormatter.HEADER1, "CI Analysis")
    new_wiki.add_header(GitWikiFormatter.HEADER3,
                        str(travis_output["TRAVIS_PULL_REQUEST"]) + "-" + str(travis_output["TRAVIS_JOB_NAME"]))
    new_wiki.add_header(GitWikiFormatter.HEADER4, "Build Metadata")

    for k, v in travis_output.items():
        new_wiki.add_key_value_pair(k, str(v))

    new_wiki.add_header(GitWikiFormatter.HEADER4, "ERROR(S)").add_lines(["t", "c", "d"])
    new_wiki.add_header(GitWikiFormatter.HEADER4, "SUCCESS(ES)").add_lines(["t", "c", "d"])
    new_wiki.add_header(GitWikiFormatter.HEADER5, "INFO").add_lines(["t", "c", "d"])
    new_wiki.add_horiz_rule()
    new_wiki.add_lines(old_wiki.get_markdown_after("CI Analysis"))
    new_wiki.write_to_file(PATH_TO_WIKI)

    print(new_wiki)
