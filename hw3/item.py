class Item:
    def __init__(self, item_name: str, item_price: int, item_hashtags: list, item_description: str):
        self.name = item_name
        self.price = item_price
        self.hashtags = item_hashtags
        self.description = item_description

    def __str__(self) -> str:
        return f'Name:\t\t\t{self.name}\n' \
               f'Price:\t\t\t{self.price}\n' \
               f'Description:\t{self.description}'
    
    def __eq__(self, other):
        '''func that check two items are equal by comparing their attributes (name, price, hashtags, and description)
        -first we check that other is an item
        - finally the func return true if all the attributes are same and false otherwise '''
        if not isinstance(other, Item):
            return False
        return(
            self.name == other.name and
            self.price == other.price and
            self.hashtags == other.hashtags and
            self.description  == other.description
        )
