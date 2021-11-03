import base64
import os
from datetime import date, datetime
import requests
import json
from jsonpath_ng import jsonpath, parse
import xlsxwriter


def searchAPIInvocation(keyword, path):
    search_headers = {
        'Authorization': 'Bearer {}'.format(access_token)
    }
    queryString = "#" + keyword
    search_params = {
        'q': queryString,
        'result_type': 'recent',
        'count': 1000

    }
    # Create the URL
    search_url = '{}1.1/search/tweets.json'.format(base_url)
    # Execute the get request
    search_resp = requests.get(search_url, headers=search_headers, params=search_params)
    # Get the data from the request
    Data = json.loads(search_resp.content)
    # Print out the data!
    # print(Data['statuses'])
    statusList = Data['statuses']

    filePath = path + "/" + keyword + ".txt"
    file = open(filePath, "w+", encoding="utf-8")
    file.write(str(Data['statuses']))

    file.close()
    print("Tweets extracted for the keyword: " + keyword)

    i = 0
    fileName = path + "/" + keyword + "_TweetDataSet.xlsx"
    workbook = xlsxwriter.Workbook(fileName)
    worksheet = workbook.add_worksheet("DataSet")

    row = 0
    col = 0

    worksheet.write(0, 0, 'Sr. No.')
    worksheet.write(0, 1, 'Tweet Timestamp')
    worksheet.write(0, 2, 'Tweet Text')
    worksheet.write(0, 3, 'Location')

    for temp in statusList:
        row += 1
        i = i + 1
        # print('Tweet Number: ' + str(i))
        worksheet.write(row, col, i)
        str1 = json.dumps(temp)
        oneResult = json.loads(str1)
        jText = parse('$.text')
        jLocation = parse('$.user.location[*]')
        jTimestamp = parse('$.created_at')
        text = jText.find(oneResult)
        location = jLocation.find(oneResult)
        timestamp = jTimestamp.find(oneResult)
        # print("Text: " + text[0].value)
        worksheet.write(row, col + 1, timestamp[0].value)
        worksheet.write(row, col + 2, text[0].value)
        j = 2
        for loc in location:
            j += 1
            # print("Location: " + loc.value)
            worksheet.write(row, col + j, loc.value)

    workbook.close()


# Define your keys from the developer portal
client_key = 'Hf4scK4x6N01rpL5PBfUipiKk'
client_secret = 'GOuxTOrYzczNxdQ3fpqlsIZqohvXqsL0iekLs3ZdJIJGnWlOzq'
# Reformat the keys and encode them
key_secret = '{}:{}'.format(client_key, client_secret).encode('ascii')

# Transform from bytes to bytes that can be printed
b64_encoded_key = base64.b64encode(key_secret)
# Transform from bytes back into Unicode
b64_encoded_key = b64_encoded_key.decode('ascii')

base_url = 'https://api.twitter.com/'
auth_url = '{}oauth2/token'.format(base_url)
auth_headers = {
    'Authorization': 'Basic {}'.format(b64_encoded_key),
    'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
}

auth_data = {
    'grant_type': 'client_credentials'
}
auth_resp = requests.post(auth_url, headers=auth_headers, data=auth_data)

print(auth_resp)

access_token = auth_resp.json()['access_token']
#hashtags = ["coronavirus", "coronavirusec", "coronaviruswho", "coronavirusoutbreak"]
hashtags = ["COVID19", "COVID_19", "COVID"]

print("Creating directory....")

#today = date.today()
#date = today.strftime("%d-%m-%Y")

now = datetime.now()
date = now.strftime("%d-%m-%Y_%H-%M-%S")

path = "C:/Users/dell pc/PycharmProjects/Update/" + date

try:
    os.mkdir(path)
except OSError:
    print("Creation of the directory %s failed" % path)
else:
    print("Successfully created the directory %s " % path)

for keyword in hashtags:
    searchAPIInvocation(keyword, path)
