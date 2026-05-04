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
        self.headers = ["name", "address", "city", "rating", "tip_count", "latitude", "longitude", "b_id"]
        self.headers_pretty = ["Business Name", "Street Address", "City", "Star Rating", "Number of Tips", "Latitude",
                               "Longitude", "Business ID"]

        # customize UI elements as needed
        self.ui.categoryList.setSelectionMode(QAbstractItemView.MultiSelection)
        self.ui.attributeList.setSelectionMode(QAbstractItemView.MultiSelection)
        self.ui.statusMsg.setText("Ready...")

        # set models for the category and attribute lists
        self.category_model = QStringListModel()
        self.ui.categoryList.setModel(self.category_model)
        self.attribute_model = QStringListModel()
        self.ui.attributeList.setModel(self.attribute_model)

        # set models for business Table view
        self.business_model = QStandardItemModel()
        self.ui.businessTable.setModel(self.business_model)

        # connect signals to handlers
        self.ui.statesList.currentIndexChanged.connect(self.on_state_changed_for_city)
        self.ui.citiesList.currentIndexChanged.connect(self.on_city_changed)
        self.ui.searchButton.clicked.connect(self.on_search_clicked)

        # create business details dialog once and reuse it
        self.business_window = BusinessDetails(self.api_client, parent=self)
        self.ui.businessTable.doubleClicked.connect(self.on_business_double_clicked)

        # wifi and price range start out empty
        self.ui.wifiList.addItem("")
        self.ui.prList.addItem("")

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
            self.request_controller.send("GET", "/api/states", self.on_states_fetched, self.on_states_error, None,
                                         self.set_status_message)

    def on_states_fetched(self, status_code, body):
        """Handle successful states response"""
        try:
            states = json.loads(body)
            self.ui.statesList.addItems(states[0]['states'])
            print(f"States loaded: {states}")

        except json.JSONDecodeError:
            print(f"Failed to parse states JSON: {body}")

    def on_states_error(self, message):
        """Handle states fetch error"""
        print(f"Error fetching states: {message}")

    def on_state_changed_for_city(self, index):
        print("Selected index in states combo box", index)
        self.request_controller.send("GET", "/api/cities?state={}".format(self.ui.statesList.currentText()),
                                     self.on_cities_fetched, self.on_cities_error, None, self.set_status_message)

    def on_city_changed(self, index):
        print("Selected index in STATES combo box:", index)
        self.request_controller.send("GET", "/api/filters?state={}&city={}".format(self.ui.statesList.currentText(),
                                                                                   self.ui.citiesList.currentText()),
                                     self.on_filters_fetched, self.on_filters_error, None, self.set_status_message)

    def on_filters_fetched(self, status_code, body):
        """Handle successful categories response"""
        try:
            filters = json.loads(body)
            # print(filters[0])
            # populate cateogry listview
            self.category_model.setStringList(filters[0]['categories'])
            self.attribute_model.setStringList(filters[1]['attributes'])
            # get wifi and price range values
            wifi_values = filters[2].get('wifi_values', [])
            price_values = filters[3].get('price_range_values',[])

            # populate wifi listview
            self.ui.wifiList.clear()
            self.ui.wifiList.addItem("")
            for i in wifi_values:
                self.ui.wifiList.addItem(i)

            # populate price range
            self.ui.prList.clear()
            self.ui.prList.addItem("")
            for i in price_values:
                self.ui.prList.addItem(i)

            # print(f"Categories loaded : {filters[0]['categories']}")
            self.set_status_message("Categories and attributes loaded successfully.")
        except json.JSONDecodeError:
            print(f"Failed to parse categories JSON: {body}")

    def on_filters_error(self, message):
        """Handles attributes fetch error"""
        print(f"Error fetching attributes {message}")

    def on_cities_fetched(self, status_code, body):
        """Handle successful cities response"""
        try:
            cities = json.loads(body)
            self.ui.citiesList.clear()
            self.ui.citiesList.addItems(cities[0]['cities'])
            print(f"Cities loaded: {cities}")
        except json.JSONDecodeError:
            print(f"Failed to parse states JSON: {body}")

    def on_cities_error(self, message):
        """Handle states fetch error"""
        print(f"Error fetching states: {message}")

    # -------------------------
    # searching the businesses
    def on_search_clicked(self):
        selected_categories = [cat.data() for cat in self.ui.categoryList.selectedIndexes()]
        selected_attributes = [attr.data() for attr in self.ui.attributeList.selectedIndexes()]
        wifi_value = self.ui.wifiList.currentText() or None
        price_value = self.ui.prList.currentText() or None

        # only include wifi and price range if attribute filters is not empty
        #attribute_filters = {}
        #wifi_value = self.ui.wifiList.currentText()
        #if wifi_value:
        #    attribute_filters['Wifi'] = wifi_value
        #price_value = self.ui.prList.currentText()
        #if price_value:
        #    attribute_filters['RestaurantsPriceRange2'] = price_value

        search_body = {
            "state": self.ui.statesList.currentText(),
            "city": self.ui.citiesList.currentText(),
            "categories": selected_categories,
            "attributes": selected_attributes,
            "wifi": wifi_value,
            "price_range": price_value,
        }
        print(f"Search POST request body: {search_body}")
        self.request_controller.send("POST", "/api/businesses", self.on_search_results, self.on_search_error,
                                     search_body)

    def on_search_results(self, status_code, body):
        """Handle successful search response"""
        try:
            results = json.loads(body)
            self.update_business_table(results)
        except json.JSONDecodeError:
            print(f"failed to parse search results in json {body}")

    def update_business_table(self, search_results):
        self.business_model.clear()
        if not search_results:
            self.set_status_message("No results found.")
            return
        businesses = search_results[0].get("businesses", [])
        self.business_model.setHorizontalHeaderLabels(self.headers_pretty)

        for row_data in businesses:
            row = [QStandardItem(str(row_data.get(col, ""))) for col in self.headers]
            self.business_model.appendRow(row)
        self.configure_results_table()
        self.set_status_message(f"Found {len(businesses)} businesses.")

    def on_search_error(self, message):
        """Handles search error"""
        self.set_status_message(f"Search failed: {message}")
        print(f"Error fetching attributes {message}")

    # -----------------------------------------------------

    def configure_results_table(self):
        """configure the table/view"""
        id_col = self.headers.index("b_id")
        id_address = self.headers.index("address")
        id_name = self.headers.index("name")
        self.ui.businessTable.resizeColumnsToContents()
        self.ui.businessTable.setColumnWidth(id_address,
                                             min(200, int(self.ui.businessTable.columnWidth(id_address) * 0.9)))
        self.ui.businessTable.setColumnWidth(id_name, min(200, int(self.ui.businessTable.columnWidth(id_name) * 0.9)))
        self.ui.businessTable.hideColumn(id_col)  # hide b_id
        self.ui.businessTable.setSelectionBehavior(QAbstractItemView.SelectRows)
        self.ui.businessTable.setSelectionMode(QAbstractItemView.SingleSelection)
        self.ui.businessTable.setEditTriggers(QAbstractItemView.NoEditTriggers)
        self.ui.businessTable.horizontalHeader().setStretchLastSection(True)

    # Handler for business details
    def on_business_double_clicked(self, index):
        row = index.row()
        model = self.ui.businessTable.model()
        business_id = model.index(row, self.headers.index('b_id')).data()
        # update businesses id in business fields
        self.business_window.load(business_id)
        self.business_window.exec()