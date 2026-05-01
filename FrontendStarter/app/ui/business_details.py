# ============================================================
# CS 3431 - Database Systems I
# Worcester Polytechnic Institute (WPI)
# 
# Sakire Arslan Ay
# All rights reserved.
# This code is provided for educational purposes only and may not be used for commercial applications.
# Note: This is starter code provided to students.
#       Modify as instructed in the assignment.
# ============================================================

import os
import json
from PySide6.QtWidgets import QDialog
from PySide6.QtCore import QFile
from PySide6.QtUiTools import QUiLoader


from app.config import SUB_WINDOW_HEIGHT, SUB_WINDOW_WIDTH
from app.apiservices.request_controller import RequestController

class BusinessDetails(QDialog):
    def __init__(self, api_client, parent=None):
        super().__init__(parent)
        self.api_client = api_client
        self.request_controller = RequestController(api_client=self.api_client, parent=self)
        self.setWindowTitle("Business Details")
        self.resize(SUB_WINDOW_WIDTH, SUB_WINDOW_HEIGHT)
                
        loader = QUiLoader()
        ui_file = QFile(os.path.join(os.path.dirname(__file__), "BusinessDetails.ui"))
        ui_file.open(QFile.ReadOnly)
        self.ui = loader.load(ui_file, None)
        ui_file.close()

        self.ui.setModal(True)

        # Connect signals to handlers
        self.ui.closeButton.clicked.connect(self.on_close_clicked)

    # Override exec to show the dialog
    def exec(self):
        return self.ui.exec()
    
    def on_close_clicked(self):
        self.ui.close()

    def load(self, business_id):
        self.request_controller.send("GET", f"/api/businesses/{business_id}", self.on_business_fetched, self.on_business_error, None, self.set_status_message)

    def on_business_fetched(self, status_code, body):
        """Handle successful business details response"""
        results = json.loads(body)[0]
        business = results.get("business", {})
        print(f"Business details fetched successfully: {business}")
        self.ui.businessName.setText(business.get("business_name",""))

    def on_business_error(self, error_message):
        """Handle error in fetching business details"""
        self.parent().show_error(f"Failed to load business details: {error_message}")

    # -----------------------------------------------------------
    # HELPER METHODS    
    def set_status_message(self, message):
        print(message)  # For debugging, print the message to the console
        if hasattr(self.ui, "statusMsg"):
            self.ui.statusMsg.setText(message)
