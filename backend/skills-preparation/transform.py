import csv
import json

def csv_to_json(csv_path, json_path):
    with open(csv_path, mode="r", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        rows = list(reader)

    with open(json_path, mode="w", encoding="utf-8") as f:
        json.dump(rows, f, ensure_ascii=False, indent=2)

if __name__ == "__main__":
    csv_path = "esco_skills_full_list.csv"   # input file
    json_path = "esco_skills_full_list.json" # output file

    csv_to_json(csv_path, json_path)
    print(f"Saved JSON to {json_path}")