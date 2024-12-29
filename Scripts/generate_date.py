import click
import os
from jinja2 import Environment, FileSystemLoader

environment = Environment(loader=FileSystemLoader("templates/"))


@click.command()
@click.option('--year', default=2023, help='The puzzle year')
@click.option('--day', default=1, help='The puzzle day')
@click.option('--response-class', type=click.Choice(['integer', 'long', 'string']), default='integer',
              help='The puzzle response type')
def cli(year, day, response_class):
    print(f'Generating data for {year} and {"{:02d}".format(day)}')

    basePath = f'../AdventOfCode/years/{year}/src/main/java/com/coding/challenges/adventofcode/year{year}/Day{"{:02d}".format(day)}'

    javaFile = f'{basePath}/Day{"{:02d}".format(day)}.java'
    sampleInputFile = f'{basePath}/input.txt'
    inputFile = f'{basePath}/sample-input.txt'

    for file in [javaFile, sampleInputFile, inputFile]:
        os.makedirs(os.path.dirname(file), exist_ok=True)

    content = environment.get_template(f'day-template.java').render(
        year=year,
        day=day,
        type=response_class.capitalize(),
        default_value=default_value(response_class)
    )
    with open(javaFile, mode="w", encoding="utf-8") as file:
        file.write(content)
        print(f"... wrote {javaFile}")
    file.close()
    with open(sampleInputFile, mode="w", encoding="utf-8") as file:
        file.write("empty")
        print(f"... wrote {sampleInputFile}")
    file.close()
    with open(inputFile, mode="w", encoding="utf-8") as file:
        file.write("empty")
        print(f"... wrote {inputFile}")
    file.close()


def default_value(response_class):
    if response_class == 'integer':
        return '0'
    if response_class == 'long':
        return '0L'
    if response_class == 'string':
        return '""'


if __name__ == '__main__':
    cli()
