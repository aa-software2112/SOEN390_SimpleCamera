import os


class TravisEnvirExtractor():

    @staticmethod
    def get_travis_variables():

        env_variables = ["TRAVIS_BRANCH",
                         "TRAVIS_BUILD_NUMBER",
                         "TRAVIS_BUILD_WEB_URL",
                         "TRAVIS_COMMIT",
                         "TRAVIS_COMMIT_RANGE",
                         "TRAVIS_JOB_NAME",
                         "TRAVIS_JOB_NUMBER",
                         "TRAVIS_JOB_WEB_URL",
                         "TRAVIS_PULL_REQUEST",
                         "TRAVIS_BUILD_STAGE_NAME"]

        output_dict = {}

        for env_variable in env_variables:
            output_dict[env_variable] = os.environ.get(env_variable)

        return output_dict

if __name__ == "__main__":
    print(TravisEnvirExtractor.get_travis_variables())
