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
import json
import os

from PySide6.QtCore import QFile
from PySide6.QtUiTools import QUiLoader
from PySide6.QtWidgets import QMainWindow


from app.config import WINDOW_TITLE, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT
from app.apiservices import APIClient
from app.apiservices.request_controller import RequestController
from app.ui.business_details import BusinessDetails


class YelpApp(QMainWindow):
    def __init__(self):
        super().__init__()
        self.resize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT)  # Set window size to match your UI design
        self.setWindowTitle(WINDOW_TITLE)

        # Initialize API client and request controller. 
        # APIClient will be used by both the main window and the business details dialog, so we create it here and pass it down.
        self.api_client = APIClient()
        self.request_controller = RequestController(api_client=self.api_client, parent=self)

        loader = QUiLoader()
        ui_file = QFile(os.path.join(os.path.dirname(__file__), "YelpApp.ui"))
        ui_file.open(QFile.ReadOnly)
        self.ui = loader.load(ui_file, None)
        ui_file.close()

        # If the loaded UI is a QMainWindow, transfer its central widget
        if isinstance(self.ui, QMainWindow):
            central = self.ui.takeCentralWidget()
            if central:
                self.setCentralWidget(central)
        else:
            self.setCentralWidget(self.ui)

        # Set up results table model
        self.headers = ["business_name", "street_address", "city", "star_rating", "num_tips", "latitude", "longitude",  "business_id"]
        self.headers_pretty = ["Business Name", "Street Address", "City", "Star Rating", "Number of Tips", "Latitude", "Longitude", "Business ID"]

    # -----------------------------------------------------------
    # HELPER METHODS
    def set_status_message(self, message):
        print(message)  # For debugging, print the message to the console

    # -----------------------------------------------------------
    #  OVERRIDE METHODS
    def showEvent(self, event):
        """Override showEvent to trigger initial data loading when the window is first shown"""
        super().showEvent(event)
        # Only fetch once on first show
        if not hasattr(self, '_initial_load_done'):
            self._initial_load_done = True
            

    
       

