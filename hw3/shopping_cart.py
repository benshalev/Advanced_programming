from item import Item
import errors

class ShoppingCart:
    def __init__(self):
        self.items_In_Cart = []

    def add_item(self, item: Item):
        '''check if item in cart,base on the __eq__ method that we build in item if it is rase exception else add the item'''
        if item in self.items_In_Cart:
            raise errors.ItemAlreadyExistsError
        self.items_In_Cart.append(item)

    def remove_item(self, item_name: str):
        '''check if item in cart if it is remove him else raise exception'''
        for item in self.items_In_Cart:
            if item_name == item.name:
                self.items_In_Cart.remove(item)
                return
        raise errors.ItemNotExistError

    def get_subtotal(self) -> int:
        '''compute the sum of all the prices (that already int type) and return this price'''
        subtotal = sum(item.price for item in self.items_In_Cart)
        return subtotal