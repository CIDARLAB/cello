#!/usr/bin/env python

import sys
sys.tracebacklimit=1
# sys.path.append('./lib')

import os
import click
import requests
import json
import ConfigParser
import getpass
from os.path import expanduser
from requests.auth import AuthBase
from requests.auth import HTTPBasicAuth
from lib import util
from lib import errors
from lib import quote_generator
from hfasta import hfasta_parser


def get_new_token(username, password, url_root):
    basic_creds = {}
    basic_creds['username'] = username
    basic_creds['password'] = password
    endpoint = url_root + "/api-token-auth/"
    r = requests.post(url=endpoint, json=basic_creds)
    response_obj = json.loads(r.text)
    token = response_obj['token']
    return token


class JWTAuth(AuthBase):
    def __init__(self, token):
        self.token = token

    def __call__(self, r):
        r.headers['Authorization'] = "JWT " + self.token
        return r



class Account(object):

    def __init__(self, username, password, namespace, url_root, verify, cert):
        # self.url_root = url_root
        # self.url_root = "https://52.91.17.52"
        self.url_root = "https://www.latticetools.org"
        self.username = username
        self.password = password
        self.namespace = namespace
        self.verify = True
        self.cert = cert
        # token = get_new_token(username, password, self.url_root)
        # self.auth = JWTAuth(token)
        self.auth = HTTPBasicAuth(username, password)

    @staticmethod
    def config_init(config):
        config_parser = ConfigParser.ConfigParser()
        config_parser.read(expanduser(config))
        username = config_parser.get("auth", "username")
        password = config_parser.get("auth", "password")
        namespace = config_parser.get("auth", "namespace") + "." + username
        url = config_parser.get("auth", "url")
        verify = expanduser(config_parser.get("auth", "verify"))
        cert = expanduser(config_parser.get("auth", "cert"))
        return Account(username, password, namespace, url, verify, cert)



def get_config():
    path_blocks = sys.argv[0].split('/')[0:-3] 
    path = ""
    for block in path_blocks:
        if block:
            path = path + "/" + block
    config = path + "/configs/client.cfg" #relative to /venv/bin/twist_api

    if not os.path.isfile(config):
        config = "configs/client.cfg" # relative to ./

    if os.path.isfile(config):
        return config
    else:
        return None



# Entry point.  
# cli invoked using the 'tw' command
# see: setup.py (tw=twist:cli)
# commands with the @cli.command() annotation are part of the 'cli' group
@click.group() 
@click.option('--config', envvar='TWIST_CONFIG', help='Specify config file (path or envvar).')
@click.version_option()
@click.pass_context
def cli(ctx, config):
    """Command-line interface for Twist ordering API"""

    # by default, use relative path (rather than ~/client.cfg)
    if not config:
        config = get_config()
        if config is None:
            return

    ctx.obj = Account.config_init(config)
    click.echo(click.style('Welcome, ' + ctx.obj.username + "\n", fg='green' ))



def get_all_response_objects(page1_response, max):
    response_objects = page1_response['results']
    all_response_objects = []
    all_response_objects.extend(response_objects)

    next_page = page1_response['next']
    while next_page is not None and len(all_response_objects) < max:
        r = requests.get(url=next_page, auth=ctx.obj.auth, verify='cert/cacert_root.crt')
        if r.status_code == 200:
            response = json.loads(r.text)
            response_objects = response['results']
            all_response_objects.extend(response_objects)                
            next_page = response['next']
    return all_response_objects



@cli.command()
@click.option('-s', '--specification', type=click.Path(exists=True), required=True, help='manufacturing request HFASTA.')
@click.option('--name', type=click.STRING, required=True, help='short name for the request.')
@click.pass_context
def post_request(ctx, specification, name):
    """Post new Request object."""
    
    hfasta = open(specification,'r').read()
    request_obj = {}
    request_obj['hfasta'] = hfasta
    request_obj['namespace'] = ctx.obj.namespace
    request_obj['request_name'] = name

    endpoint = ctx.obj.url_root + "/hfasta/"

    manufacturingRequestObject = hfasta_parser.parse_hfasta_text_to_json_request( hfasta, ctx.obj.namespace, name)
    r = requests.post(url=ctx.obj.url_root + "/requests/", json=manufacturingRequestObject, auth=ctx.obj.auth, verify=True)

    # r = requests.post(url=ctx.obj.url_root + "/hfasta/", json=request_obj, auth=ctx.obj.auth, verify=True)

    if r.status_code == 201:
        response_obj = json.loads(r.text)
        response_objects = []
        response_objects.append(response_obj)
        click.echo(util.get_objects_as_table(ctx, response_objects))
        click.echo(util.get_constructs_as_table(ctx, response_obj))
        click.echo(click.style('\nSuccessful "post_request"\n', fg='green'))
    else:
        click.echo(click.style('error in "post_request"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))
        click.echo(r.text)


@click.pass_context
def show_request(ctx, request_uid):
    """Get a manufacturing request by ID."""

    endpoint = ctx.obj.url_root + "/requests/" + request_uid + "/"
    r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)

    if r.status_code == 200:
        response_obj = json.loads(r.text)
        for construct in response_obj['constructs']:
            print construct['constructUID']
        response_objects = []
        response_objects.append(response_obj)
        click.echo(util.get_objects_as_table(ctx, response_objects))
        click.echo(util.get_constructs_as_table(ctx, response_obj))
        click.echo(click.style('\nSuccessful "get_request"\n', fg='green'))
    else:
        click.echo(click.style('error in "get_request"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))



request_states = ['VALIDATED', 'TRIAGED', 'QUOTE_REQUESTED', 'QUOTED']
@cli.command()
@click.option('--name', type=click.STRING, help='name of Request object.')
@click.option('--state', type=click.Choice(request_states), help='filter by state.')
@click.option('--max', type=click.INT, default=10, help='max number to show.')
@click.pass_context
def show_requests(ctx, name, state, max):
    """Get Request object(s)."""

    if name:
        request_uid = ctx.obj.namespace + "." + name
        ctx.invoke(show_request, request_uid=request_uid) # get by uid

    else: # get all
        endpoint = ctx.obj.url_root + "/requests/"

        print endpoint
        print ctx.obj.verify

        if state:
            endpoint = endpoint + "?state=" + state

        r = requests.get(url=endpoint, auth=ctx.obj.auth, verify='cert/cacert_root.crt')
        
        if r.status_code == 200:
            
            response = json.loads(r.text)
            all_response_objects = get_all_response_objects(response, max)
            
            if not all_response_objects:
                click.echo('no request objects found.')
            else:
                click.echo(util.get_objects_as_table(ctx, all_response_objects))
            click.echo(click.style('\nSuccessful "get_requests"\n', fg='green'))
        else:
            click.echo(click.style('error in "get_requests"', fg='red'))
            click.echo(click.style(errors.error_message(r.text), fg='red'))




@click.pass_context
def update_request(ctx, name, state):
    """Update state of the request."""

    if not name and not request_uid:
        click.echo(click.style('to update state, specify --name or --request-uid.', fg='red'))
        return

    if name is not None:
        uid = ctx.obj.namespace + "." + name
    
    elif request_uid is not None:
        uid = request_uid

    endpoint = ctx.obj.url_root + "/requests/" + uid + "/"
    request_obj = {}
    request_obj['requestUID'] = uid
    request_obj['sequences'] = []
    request_obj['designs'] = []
    request_obj['constructs'] = []
    request_obj['samples'] = []
    request_obj['containers'] = []
    request_obj['vectors'] = []
    request_obj['annotations'] = []
    request_obj['state'] = state
    r = requests.put(url=endpoint, json=request_obj, auth=ctx.obj.auth, verify=True)
    return r
    
    
@cli.command()
@click.option('--name', type=click.STRING, required=True, help='move Request to QUOTE_REQUESTED state.')
@click.pass_context
def get_quote(ctx, name):
    """Move Request to QUOTE_REQUESTED state."""
    
    r = ctx.invoke(update_request, name=name, state="QUOTE_REQUESTED")
    if r.status_code == 200:
        response_obj = json.loads(r.text)
        response_objects = []
        response_objects.append(response_obj)
        click.echo(util.get_objects_as_table(ctx, response_objects))
        click.echo(util.get_constructs_as_table(ctx, response_obj))
        click.echo(click.style('\nSuccessful "quote_request" for ' + name + '\n', fg='green'))
    elif r.status_code == 403:
        click.echo(click.style(name + ': request must be in the TRIAGED state to request a quote.', fg='red'))
        #click.echo(click.style(errors.error_message(r.text), fg='red'))
    else:
        click.echo(click.style('error in "quote_request"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))


@cli.command()
@click.option('--name', type=click.STRING, required=True, help='move Request to ABORTED state.')
@click.pass_context
def cancel_request(ctx, name):
    """Move Request to ABORTED state."""
    
    r = ctx.invoke(update_request, name=name, state="ABORTED")
    if r.status_code == 200:
        response_obj = json.loads(r.text)
        response_objects = []
        response_objects.append(response_obj)
        click.echo(util.get_objects_as_table(ctx, response_objects))
        click.echo(util.get_constructs_as_table(ctx, response_obj))
        click.echo(click.style('\nSuccessful "cancel_request" for ' + name + '\n', fg='green'))
    else:
        click.echo(click.style('error in "cancel_request"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))




@click.pass_context
def show_quote(ctx, quote_uid):
    """Get Quote object by UID."""

    endpoint = ctx.obj.url_root + "/quotes/" + quote_uid + "/"
    r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)
    if r.status_code == 200:
        quote_obj = json.loads(r.text)
        click.echo(util.get_quote_as_table(ctx, quote_obj))
        click.echo("")
        click.echo(util.get_quotelets_as_table(ctx, quote_obj))
        click.echo("")
        click.echo(util.get_quoted_constructs_as_table(ctx, quote_obj))
        click.echo(click.style('\nSuccessful "get_quote"\n', fg='green'))
    else:
        click.echo(click.style('error in "get_quote"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))



@cli.command()
@click.option('--quote-uid', type=click.STRING, help='full name of Quote object.')
@click.option('--max', type=click.INT, default=10, help='max number to show.')
@click.pass_context
def show_quotes(ctx, quote_uid, max):
    """Get Quote object(s)."""

    if quote_uid is not None:
        ctx.invoke(show_quote, quote_uid=quote_uid)

    else:
        endpoint = ctx.obj.url_root + "/quotes/"
        r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)
        if r.status_code == 200:

            response = json.loads(r.text)
            all_response_objects = get_all_response_objects(response, max)

            if not all_response_objects:
                click.echo('no quote objects found.')
            else:
                click.echo(util.get_quotes_as_table(ctx, all_response_objects))
            click.echo(click.style('\nSuccessful "get_quotes"\n', fg='green'))
        else:
            click.echo(click.style('error in "get_quotes"', fg='red'))
            click.echo(click.style(errors.error_message(r.text), fg='red'))



@cli.command()
@click.option('--quote-uid', type=click.STRING, required=True, help='full name of Quote object.')
@click.option('--payment-uid', type=click.STRING, required=True, help='full name of Payment object.')
@click.argument('quotelets', nargs=-1) # if missing, order all
@click.pass_context
def post_order(ctx, quote_uid, payment_uid, quotelets):
    """Post new Order object."""

    quotelets = [x.encode('ascii') for x in quotelets]

    endpoint = ctx.obj.url_root + "/quotes/" + quote_uid + "/"
    r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)

    if r.status_code == 200:
        quote_obj = json.loads(r.text)
        order = {}
        order['orderUID'] = quote_obj['requestUID'] + ".order"
        order['quoteUID'] = quote_uid
        order['paymentUID'] = payment_uid
        order['quotelets'] = []
    else:
        click.echo(click.style('error in "post_order"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))
        return

    if not quotelets:
        # add all quoteletUIDs from the Quote object
        qs = quote_obj['quotelets']
        for q in qs:
            order['quotelets'].append( q['quoteletUID'] )
        
    else:
        for relative_quotelet_uid in quotelets:
            for q in quote_obj['quotelets']:
                quid = q['quoteletUID']
                if quid == quote_uid + "." + relative_quotelet_uid:
                    order['quotelets'].append(quid)

        if not order['quotelets']:
            click.echo(click.style('quotelets ' + str(quotelets) + ' were not identified, order was not placed.', fg='red'))
            return


    # post the Order
    endpoint = ctx.obj.url_root + "/orders/"
    r = requests.post(url=endpoint, json=order, auth=ctx.obj.auth, verify=True)

    if r.status_code == 201:
        order_obj = json.loads(r.text)
        click.echo(util.get_order_as_table(ctx, order_obj))
        click.echo(click.style('\nSuccessful "post_order"\n', fg='green'))
    else:
        click.echo(click.style('error in "post_order"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))




@click.pass_context
def show_order(ctx, order_uid):
    """Get Order object by UID."""

    endpoint = ctx.obj.url_root + "/orders/" + order_uid + "/"
    r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)
    if r.status_code == 200:
        order_obj = json.loads(r.text)
        click.echo(util.get_order_as_table(ctx, order_obj))
        click.echo(click.style('\nSuccessful "show_order"\n', fg='green'))
    else:
        click.echo(click.style('error in "show_order"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))



@cli.command()
@click.option('--name', type=click.STRING, help='name of Order object.')
@click.option('--order-uid', type=click.STRING, help='full name of Order object.')
@click.option('--max', type=click.INT, default=10, help='max number to show.')
@click.pass_context
def show_orders(ctx, name, order_uid, max):
    """Get Order object(s)."""

    if name is not None:
        order_uid = ctx.obj.namespace + "." + name
        ctx.invoke(show_order, order_uid=order_uid)

    elif order_uid is not None:
        ctx.invoke(show_order, order_uid=order_uid)        

    else:
        endpoint = ctx.obj.url_root + "/orders/"
        r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)
        if r.status_code == 200:

            response = json.loads(r.text)
            all_response_objects = get_all_response_objects(response, max)

            if not all_response_objects:
                click.echo('no order objects found.')
            else:
                click.echo(util.get_orders_as_table(ctx, all_response_objects))
            click.echo(click.style('\nSuccessful "show_orders"\n', fg='green'))
        else:
            click.echo(click.style('error in "show_orders"', fg='red'))
            click.echo(click.style(errors.error_message(r.text), fg='red'))




@cli.command()
@click.option('--name', type=click.STRING, required=True, help='move Order to ABORTED state.')
@click.pass_context
def cancel_order(ctx, name):
    """Move Order to ABORTED state."""

    order_uid = ctx.obj.namespace + "." + name
    endpoint = ctx.obj.url_root + "/orders/" + order_uid + "/"
    r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)
    if r.status_code == 200:
        order_obj = json.loads(r.text)
        order_obj['state'] = "ABORTED"
        r = requests.put(url=endpoint, json=order_obj, auth=ctx.obj.auth, verify=True)
        if r.status_code == 200:
            response_obj = json.loads(r.text)
            click.echo(util.get_order_as_table(ctx, response_obj))
            click.echo(click.style('\nSuccessful "cancel_order"\n', fg='green'))
        else:
            click.echo(click.style('error in "cancel_order"', fg='red'))
            click.echo(click.style(errors.error_message(r.text), fg='red'))
    else:
        click.echo(click.style('error in "cancel_order"', fg='red'))
        click.echo(click.style('order UID ' + order_uid +' not found', fg='red'))



@cli.command()
@click.option('--name', type=click.STRING, required=True, help='name of Order object.')
@click.pass_context
def order_received(ctx, name):
    """Move Order to RECEIVED state."""
    
    uid = ctx.obj.namespace + "." + name
    
    endpoint = ctx.obj.url_root + "/orders/" + uid + "/"
    r = requests.get(url=endpoint, auth=ctx.obj.auth, verify=True)
    
    if r.status_code == 200:
        order_obj = json.loads(r.text)
    else:
        click.echo(click.style('order not found', fg='red'))
        return

    order_obj['state'] = "RECEIVED"
    r = requests.put(url=endpoint, json=order_obj, auth=ctx.obj.auth, verify=True)

    if r.status_code == 200:
        response_obj = json.loads(r.text)
        click.echo(util.get_order_as_table(ctx, response_obj))
        click.echo(click.style('\nSuccessful "order_received"', fg='green'))
    else:
        print r.text
        click.echo(click.style('error in "order_received"', fg='red'))
        click.echo(click.style(errors.error_message(r.text), fg='red'))




if __name__ == '__main__':
    cli()

"""End"""
