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

from PySide6.QtCore import QFile, QStringListModel
from PySide6.QtUiTools import QUiLoader
from PySide6.QtWidgets import QAbstractItemView, QMainWindow
from PySide6.QtGui import QStandardItemModel, QStandardItem


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

        # customize ui elements as needed
        self.ui.categoryList.setSelectionMode(QAbstractItemView.MultiSelection)
        self.ui.statusMsg.setText("Ready...")

        # set Models for the category and attribute lists
        self.category_model = QStringListModel()
        self.ui.categoryList.setModel(self.category_model)

        # set Model for the Business TableView
        self.business_model = QStandardItemModel()
        self.ui.businessTable.setModel(self.business_model)

        # connect signals to handlers
        self.ui.statesList.currentIndexChanged.connect(self.on_state_changed)
        self.ui.searchButton.clicked.connect(self.on_search_clicked)

        # create the business details dialog once and reuse it
        self.business_window = BusinessDetails(self.api_client, parent=self)
        self.ui.businessTable.doubleClicked.connect(self.on_business_double_clicked)

    # -----------------------------------------------------------
    # HELPER METHODS
    def set_status_message(self, message):
        self.ui.statusMsg.setText(message)

    # -----------------------------------------------------------
    #  OVERRIDE METHODS
    def showEvent(self, event):
        """Override showEvent to trigger initial data loading when the window is first shown"""
        super().showEvent(event)
        # Only fetch once on first show
        if not hasattr(self, '_initial_load_done'):
            self._initial_load_done = True
            self.request_controller.send("GET", "/api/states", self.on_states_fetched, self.on_states_error, None, self.set_status_message)

    def on_states_fetched(self, status_code, body):
        """Handle successful status response"""
        try:
            states = json.loads(body)
            # populate your state combo box here
            # assuming the response has a 'states' key with a list of state names
            self.ui.statesList.addItems(states[0]['states'])
            print(f"States loaded: {states}")
        except json.JSONDecodeError:
            print(f"Failed to parse states JSON: {body}")
    
    def on_states_error(self, message):
        """Handle states fetch error"""
        print(f"Error fetching states: {message}")

    def on_state_changed(self, index):
        print("Selected index in STATES combo box:", index)
        # Fetch categories and attributes from /api/filters enpoint using the selected state and city
        self.request_controller.send("GET", "/api/filters?state={}".format(self.ui.statesList.currentText()),
                        self.on_filters_fetched, self.on_filters_error, None, self.set_status_message)

    # Handlers for categories
    def on_filters_fetched(self, status_code, body):
        """Handle successful categories response"""
        try:
            filters = json.loads(body)
            # print(filters[0])
            # populate your category listview here
            # assuming the response has a 'categories' key with a list of category names
            self.category_model.setStringList(filters[0]['categories'])
            # print (f"Categories loaded: {filters[0]['categories']}")
            self.set_status_message("Categories loaded successfully")
        except json.JSONDecodeError:
            print(f"Failed to parse categories JSON: {body}")

    def on_filters_error(self, message):
        """Handle attributes fetch error"""
        print(f"Error fetching attributes: {message}")

    # -----------------------------------------------------------
    # Handlers for business search
    def on_search_clicked(self):
        # Gather selected filters and send search request to /api/search endpoint
        selected_categories = [cat.data() for cat in self.ui.categoryList.selectedIndexes()]
        search_body = {
            "state": self.ui.statesList.currentText(),
            "categories": selected_categories
        }
        print(f"Search POST request body: {search_body}")
        self.request_controller.send("POST", "/api/businesses", self.on_search_results, self.on_search_error, search_body)

    def on_search_results(self, status_code, body):
        """Handle successful search response"""
        try:
            results = json.loads(body)
            self.update_business_table(results)
        except json.JSONDecodeError:
            print(f"Failed to parse search results JSON: {body}")

    def update_business_table(self, search_results):
        """Update the tablView with business search results"""
        self.business_model.clear()
        if not search_results:
            self.set_status_message("No results found")
            return
        businesses = search_results[0].get('businesses',[])
        self.business_model.setHorizontalHeaderLabels(self.headers_pretty)
        for row_data in businesses:
            row = [QStandardItem(str(row_data.get(col,""))) for col in self.headers]
            self.business_model.appendRow(row)
        # configure the tableView doesn't work here for some reason
        self.configure_results_table()

        self.set_status_message(f"Found {len(businesses)} businesses")

    def on_search_error(self, message):
        """Handle search error"""
        self.set_status_message(f"Search failed: {message}")
        print(f"Error performing search: {message}")

    # -----------------------------------------------------------
    def configure_results_table(self):
        """Configure the tableView"""
        id_col = self.headers.index("business_id")
        id_address = self.headers.index("street_address")
        id_name = self.headers.index("business_name")
        self.ui.businessTable.resizeColumnsToContents()
        self.ui.businessTable.setColumnWidth(id_address, min(200,int(self.ui.businessTable.columnWidth(id_address) * 0.9)))
        self.ui.businessTable.setColumnWidth(id_name, min(200,int(self.ui.businessTable.columnWidth(id_address) * 0.9)))
        self.ui.businessTable.hideColumn(id_col) # hide b_id, we need to keep b_id to get the selected business
        self.ui.businessTable.setSelectionBehavior(QAbstractItemView.SelectRows) # select whole rows
        self.ui.businessTable.setSelectionMode(QAbstractItemView.SingleSelection) # one row at a time
        self.ui.businessTable.setEditTriggers(QAbstractItemView.NoEditTriggers) # make the table read-only
        self.ui.businessTable.horizontalHeader().setStretchLastSection(True)

    # -----------------------------------------------------------
    # Handlers for business details
    def on_business_double_clicked(self, index):
        row = index.row()
        model = self.ui.businessTable.model()
        business_id = model.index(row, self.headers.index("business_id")).data()
        # load business details in the business details dialog and show it modally
        self.business_window.load(business_id) # load business details using the business_id
        self.business_window.exec() # show modally