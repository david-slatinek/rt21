from __init__ import SignCollection

if __name__ == "__main__":
    data = SignCollection.find({})
    for document in data:
        # SignCollection.update_one({'_id': document["_id"]}, {'$set': {"longitude": float(document["longitude"])}})
        print(document)
