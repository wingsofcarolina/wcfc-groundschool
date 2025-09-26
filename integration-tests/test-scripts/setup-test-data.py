#!/usr/bin/env python3
"""
Setup test data in MongoDB for WCFC Manuals integration tests
"""

import json
import os
import sys
import uuid

try:
    from pymongo import MongoClient
except ImportError:
    print("PyMongo not available. Please install it with: pip install pymongo")
    sys.exit(1)

def setup_mongodb_data():
    """Setup test data in MongoDB"""
    print("Setting up MongoDB test data...")
    
    # Connect to MongoDB
    client = MongoClient('mongodb://localhost:27017/')
    db = client['wcfc-groundschool']
    
    # Clear existing data
    db.Students.drop()
    db.Admins.drop()
    db.VerificationCode.drop()
    db.counters.drop()
    
    # Create test student data (for the private section)
    test_students = [
        {
            "studentId": 1001,
            "name": "Test User",
            "email": "test@example.com",
            "section": "private",
            "uuid": str(uuid.uuid4())
        },
        {
            "studentId": 1002,
            "name": "John Pilot",
            "email": "john.pilot@example.com",
            "section": "commercial",
            "uuid": str(uuid.uuid4())
        }
    ]
    
    db.Students.insert_many(test_students)
    print(f"Inserted {len(test_students)} test student records")
    
    # Create test admin data
    test_admins = [
        {
            "adminId": 2001,
            "name": "Admin User",
            "email": "admin@example.com",
            "uuid": str(uuid.uuid4())
        }
    ]
    
    db.Admins.insert_many(test_admins)
    print(f"Inserted {len(test_admins)} test admin records")
    
    # Create sequence counters
    db.counters.insert_many([
        {"_id": "students", "seq": 1002},
        {"_id": "admins", "seq": 2001}
    ])
    
    print("MongoDB test data setup completed successfully!")
    
    client.close()

if __name__ == "__main__":
    try:
        setup_mongodb_data()
        print("Test data setup completed successfully!")
    except Exception as e:
        print(f"Error setting up test data: {e}")
        sys.exit(1)
