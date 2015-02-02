import requests
import json
import sys
from time import sleep
from random import randrange

#host = 'http://127.0.0.1:8080'
host = 'http://54.172.86.50:8080'
headers = {"content-type":"application/json"}
N = 5


# Generate a route from the file specified in the first argument

routes_file = open(sys.argv[1], 'r')
route_name = routes_file.readline().strip()
route = [(float(line.split()[0]), float(line.split()[1])) for line in routes_file]
print route_name
print route



# Register street
payload = {'streetName' : route_name}
r = requests.post(host + '/register', data=json.dumps(payload), headers=headers)

print r.text
response = json.loads(r.text)
print response
behavior = response['behavior']
if behavior != 'sample':
    points = response['verificationPoints']

count = 0
for latitude, longitude in route:
    if behavior == 'sample':
        if count == 0:
            pointa = {'longitude':route[0][1], 'latitude':route[0][0]}
            pointb = {'longitude':route[1][1], 'latitude':route[1][0]}
            payload = {'streetName':route_name, \
                'startlong':pointa['longitude'], 'startlat':pointa['latitude'], \
                'endlong': pointb['longitude'], 'endlat':pointb['latitude'], \
                'milliseconds': 0}
            print json.dumps(payload)
            r = requests.post(host + '/recommendation', data=json.dumps(payload), headers=headers)
            print "received recommendation"
            print r.text
            count += 1

        payload = {'streetName': route_name, \
                    'sample':{'longitude':longitude, 'latitude':latitude}}
        r = requests.post(host + '/streetsample', data=json.dumps(payload), headers=headers)
        sleep(2)
    else:
        pointa = points.pop(0)
        try:
            pointb = points[0]
        except (IndexError):
            break

        if count == 0:
            count = N
            payload = {'streetName':route_name, \
                'startlong':pointa['longitude'], 'startlat':pointa['latitude'], \
                'endlong': pointb['longitude'], 'endlat':pointb['latitude'], \
                'milliseconds': 0}
            print json.dumps(payload)

            r = requests.post(host + '/recommendation', data=json.dumps(payload), headers=headers)
            print "received recommendation"
            print r.text
        count -= 1

        print "Evaluating next segment"
        time_taken = randrange(7000 , 12000)
        sleep(float(time_taken) / 1000)
        print time_taken

        payload = {'streetName':route_name, \
            'startlong':pointa['longitude'], 'startlat':pointa['latitude'], \
            'endlong': pointb['longitude'], 'endlat':pointb['latitude'], \
            'milliseconds': time_taken}

        r = requests.post(host + '/evaluate', data=json.dumps(payload), headers=headers)
        print "Segment Evaluated"

