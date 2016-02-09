import requests
import json
import sys
import os
import click
from requests.auth import HTTPBasicAuth
from Bio import SeqIO
import io

class CtxObject(object):
    def __init__(self):
        self.url_root = "http://127.0.0.1:8080"

        self.username = os.environ.get('CELLOUSER')
        self.password = os.environ.get('CELLOPASS')

        self.auth = HTTPBasicAuth(self.username, self.password)


def result(r):
    click.echo(click.style(str(r.status_code), fg='green' ))
    try:
        arr = json.loads(r.text)
        click.echo(json.dumps(arr, indent=4))
    except:
        click.echo(r.text)


@click.group() 
@click.version_option()
@click.pass_context
def cli(ctx):
    """Command-line interface for Cello genetic circuit design"""
    ctx.obj = CtxObject()



@cli.command()
@click.option('--jobid', type=click.STRING, help='job name.')
@click.option('--keyword', type=click.STRING, help='file name contains substring.')
@click.option('--extension', type=click.STRING, help='file name ends with substring.')
@click.option('--filename', type=click.STRING, help='file name.')
@click.pass_context
def get_results(ctx, jobid, keyword, extension, filename):

    if jobid == None:
        endpoint = ctx.obj.url_root + "/results"
        r = requests.get(endpoint, auth=ctx.obj.auth)
        result(r)

    elif jobid != None and filename == None:
        params = {}
        if keyword:
            params['keyword'] = keyword
        if extension:
            params['extension'] = extension

        endpoint = ctx.obj.url_root + "/results/" + jobid
        r = requests.get(endpoint, params=params, auth=ctx.obj.auth)
        result(r)

    elif jobid != None and filename != None:
        endpoint = ctx.obj.url_root + "/results/" + jobid + "/" + filename
        r = requests.get(endpoint, auth=ctx.obj.auth)
        result(r)



@cli.command()
@click.option('--name', type=click.STRING, help='promoter name.')
@click.pass_context
def get_inputs(ctx, name):

    if name:
        filename = "input_" + name + ".txt"
        endpoint = ctx.obj.url_root + "/in_out/" + filename
        r = requests.get(endpoint, auth=ctx.obj.auth)
        result(r)

    else:
        params = {}
        params['keyword'] = "input_"
        params['extension'] = "txt"
        endpoint = ctx.obj.url_root + "/in_out"
        r = requests.get(endpoint, params=params, auth=ctx.obj.auth)
        result(r)


@cli.command()
@click.pass_context
@click.option('--name', type=click.STRING, help='output name.')
def get_outputs(ctx, name):
    
    if name:
        filename = "output_" + name + ".txt"
        endpoint = ctx.obj.url_root + "/in_out/" + filename
        r = requests.get(endpoint, auth=ctx.obj.auth)
        result(r)

    else:
        params = {}
        params['keyword'] = "output_"
        params['extension'] = "txt"
        endpoint = ctx.obj.url_root + "/in_out"
        r = requests.get(endpoint, params=params, auth=ctx.obj.auth)
        result(r)


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='promoter name.')
@click.option('--low', type=click.FLOAT, required=True, help='low REU.')
@click.option('--high', type=click.FLOAT, required=True, help='high REU.')
@click.option('--dnaseq', type=click.STRING, required=True, help='dna sequence.')
@click.pass_context
def post_input(ctx, name, low, high, dnaseq):
    filename = "input_" + name + ".txt"
    input_string = name + " " + str(low) + " " + str(high) + " " + dnaseq + "\n"
    params = {}
    params['filetext'] = input_string
    endpoint = ctx.obj.url_root + "/in_out/" + filename
    r = requests.post(endpoint, params=params, auth=ctx.obj.auth)
    result(r)


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='output name.')
@click.option('--dnaseq', type=click.STRING, required=True, help='dna sequence.')
@click.pass_context
def post_output(ctx, name, dnaseq):
    filename = "output_" + name + ".txt"
    output_string = name + " " + dnaseq + "\n"
    params = {}
    params['filetext'] = output_string
    endpoint = ctx.obj.url_root + "/in_out/" + filename
    r = requests.post(endpoint, params=params, auth=ctx.obj.auth)
    result(r)


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='promoter name.')
@click.pass_context
def delete_input(ctx, name):
    filename = "input_" + name + ".txt"
    endpoint = ctx.obj.url_root + "/in_out/" + filename
    r = requests.delete(endpoint, auth=ctx.obj.auth)
    result(r)    


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='output name.')
@click.pass_context
def delete_output(ctx, name):
    filename = "output_" + name + ".txt"
    endpoint = ctx.obj.url_root + "/in_out/" + filename
    r = requests.delete(endpoint, auth=ctx.obj.auth)
    result(r)




@cli.command()
@click.option('--verilog', type=click.Path(exists=True), required=True, help='verilog file path.')
@click.pass_context
def netsynth(ctx, verilog):
    endpoint = ctx.obj.url_root + "/netsynth"
    verilog_text = open(verilog, 'r').read()

    params = {}
    params['verilog_text'] = verilog_text 

    r = requests.post(endpoint, params=params, auth=ctx.obj.auth)
    result(r)


@cli.command()
@click.option('--jobid', type=click.STRING, required=True, help='job id/name.')
@click.option('--verilog', type=click.Path(exists=True), required=True, help='verilog file.')
@click.option('--inputs', type=click.Path(exists=True), required=True, help='input promoters file.')
@click.option('--outputs', type=click.Path(exists=True), required=True, help='output genes file.')
@click.option('--options', type=click.STRING, help='additional dash-separated options.')
@click.pass_context
def submit(ctx, jobid, verilog, inputs, outputs, options):

    endpoint = ctx.obj.url_root + "/submit"

    inputs_text = open(inputs, 'r').read()
    outputs_text = open(outputs, 'r').read()
    verilog_text = open(verilog, 'r').read()


    params = {}
    params['id'] = jobid
    params['input_promoter_data'] = inputs_text
    params['output_gene_data'] = outputs_text
    params['verilog_text'] = verilog_text
    params['options'] = options

    r = requests.post(endpoint, params=params, auth=ctx.obj.auth)
    result(r)


@cli.command()
@click.option('--jobid', type=click.STRING, required=True, help='job id/name.')
@click.option('--assignment', type=click.STRING, help='e.g. A000')
@click.pass_context
def show_parts(ctx, jobid, assignment):

    params = {}
    if assignment:
        if len(assignment) is not 4 or not assignment.startswith('A'):
            click.echo('invalid assignment name')
            return
        else:
            params['keyword'] = assignment

    params['extension'] = 'part_list.txt'

    endpoint = ctx.obj.url_root + "/results/" + jobid
    r = requests.get(endpoint, params=params, auth=ctx.obj.auth)
    if r.status_code is 200:
        filenames = json.loads(r.text)
        
        for filename in filenames:
            endpoint = ctx.obj.url_root + "/results/" + jobid + "/" + filename
            r = requests.get(endpoint, auth=ctx.obj.auth)
            name = r.text.split('[')[0]
            parts = '[' + r.text.split('[')[1]
            parts = parts.replace('[', '[\"')
            parts = parts.replace(']', '\"]')
            parts = parts.replace(', ', '\", \"')
            parts = json.loads(parts)
            click.echo(json.dumps(parts, indent=4))



@click.pass_context
def show_files_contents(ctx, jobid, assignment, extension):
    params = {}
    if assignment:
        if len(assignment) is not 4 or not assignment.startswith('A'):
            click.echo('invalid assignment name')
            return
        else:
            params['keyword'] = assignment

    params['extension'] = extension


    endpoint = ctx.obj.url_root + "/results/" + jobid
    r = requests.get(endpoint, params=params, auth=ctx.obj.auth)
    if r.status_code is 200:
        filenames = json.loads(r.text)
        
        for filename in filenames:
            endpoint = ctx.obj.url_root + "/results/" + jobid + "/" + filename
            r = requests.get(endpoint, auth=ctx.obj.auth)
            click.echo(r.text)
            click.echo("\n================================================================================\n")


@cli.command()
@click.option('--jobid', type=click.STRING, required=True, help='job id/name.')
@click.option('--assignment', type=click.STRING, help='e.g. A000')
@click.pass_context
def show_circuit_info(ctx, jobid, assignment):
    ctx.invoke(show_files_contents, jobid=jobid, assignment=assignment, extension='logic_circuit.txt')



@cli.command()
@click.option('--jobid', type=click.STRING, required=True, help='job id/name.')
@click.option('--assignment', type=click.STRING, help='e.g. A000')
@click.pass_context
def show_reu_table(ctx, jobid, assignment):
    ctx.invoke(show_files_contents, jobid=jobid, assignment=assignment, extension='reutable.txt')


@cli.command()
@click.option('--jobid', type=click.STRING, required=True, help='job id/name.')
@click.option('--filename', type=click.STRING, required=True, help='file name (.ape)')
@click.option('--seq', is_flag=True, help='also print the dna sequence')
@click.pass_context
def read_genbank(ctx, jobid, filename, seq):

    r = requests.get(ctx.obj.url_root + "/resultsroot", auth=ctx.obj.auth)
    server_root = r.text
    filepath = server_root + "/" + ctx.obj.username + "/" + jobid + "/" + filename
    
    gb_record = SeqIO.read(open(filepath,"r"), "genbank")

    if seq:
        for gb_feature in gb_record.features:
            print gb_feature.location, gb_feature.type, gb_feature.qualifiers['label'], gb_feature.extract(gb_record.seq)
    else:
        for gb_feature in gb_record.features:
            print gb_feature.location, gb_feature.type, gb_feature.qualifiers['label']


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='UCF name')
@click.option('--filepath', type=click.Path(exists=True), required=True, help='UCF file.')
@click.pass_context
def post_ucf(ctx, name, filepath):

    if not name.endswith(".UCF.json"):
        click.echo("UCF file name must end with the extension .UCF.json")
        return

    filetext = open(filepath, 'r').read()
    filejson = json.loads(filetext)

    params = {}
    params['filetext'] = json.dumps(filejson)

    endpoint = ctx.obj.url_root + "/ucf/" + name
    r = requests.post(endpoint, data=params, auth=ctx.obj.auth)
    result(r)


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='UCF name')
@click.pass_context
def validate_ucf(ctx, name):

    if not name.endswith(".UCF.json"):
        click.echo("UCF file name must end with the extension .UCF.json")
        return

    endpoint = ctx.obj.url_root + "/ucf/" + name + "/validate"
    r = requests.get(endpoint, auth=ctx.obj.auth)
    result(r)


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='UCF name')
@click.pass_context
def delete_ucf(ctx, name):

    if not name.endswith(".UCF.json"):
        click.echo("UCF file name must end with the extension .UCF.json")
        return

    endpoint = ctx.obj.url_root + "/ucf/" + name
    r = requests.delete(endpoint, auth=ctx.obj.auth)
    result(r)


if __name__ == '__main__':
    # post_job(sys.argv[1])
    # test_hello()
    # post_netsynth()
    # post_test()
    print "main"

