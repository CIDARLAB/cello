from setuptools import setup, find_packages

setup(
    name='cello',
    version='0.1',
    packages=find_packages(exclude=["tests"]),
    include_package_data=True,
    install_requires=[
        'Click',
        'requests',
    ],
    entry_points='''
        [console_scripts]
        cello=cello_client:cli
    ''',
)