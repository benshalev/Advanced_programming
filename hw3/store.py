import yaml

from item import Item
from shopping_cart import ShoppingCart
import errors

class Store:
    def __init__(self, path):
        with open(path) as inventory:
            items_raw = yaml.load(inventory, Loader=yaml.FullLoader)['items']
        self._items = self._convert_to_item_objects(items_raw)
        self._shopping_cart = ShoppingCart()

    @staticmethod
    def _convert_to_item_objects(items_raw):
        return [Item(item['name'],
                     int(item['price']),
                     item['hashtags'],
                     item['description'])
                for item in items_raw]

    def get_items(self) -> list:
        return self._items
    

    def get_flat_tags(self , item_list: list) -> list:
        '''A func that get the list of items
        - take the list of hashtags that each item have in the list (that the func get)
        - for each tag in the hashtags list (of each item) put them in one flat list (with repeats)
        - finally return the list'''
        flattened_Tag_list = [tags for item in item_list for tags in item.hashtags]
        return flattened_Tag_list
    
    def sum_of_appears_in_list(self, item: Item, list_of_tags: list) -> int:
        '''A func that get one item and a list of tags (with repeats)
        - count how many times (in total) each tag in the single items appear in the list of tags
        - make a sum of all of them (if the hashtags list is empty it will be 0)
        - finally return the total sum
        - url of read about count: https://www.programiz.com/python-programming/methods/list/count'''
        num = sum(list_of_tags.count(tag) for tag in item.hashtags)
        return num
    
    def sort_list(self, match_item_list: list,) -> list:
        '''A func that get the list of the items that match to the search (in store, not in cart, and have substring of the given name of search by func) and a list of the tags that in the cart
        - first make a list from all of the hashtags of of the items in the cart
        - then sort by lexicography sorted (using the name as the key)
        - then take the sorted list of the items by the amount of appearance of hashtags that each item that in the match list and the tags in cart (from the biggest to the smallest (reverse)) (by using sum_of_appears_in_list method)
        - finally return the total sum
        - url of read lambda sort: https://www.freecodecamp.org/news/lambda-sort-list-in-python/ '''
        Tags_in_cart = self.get_flat_tags(self._shopping_cart.items_In_Cart)
        match_item_list.sort(key=lambda y: y.name)
        sorted_list = sorted(match_item_list, key=lambda x: self.sum_of_appears_in_list(x , Tags_in_cart),reverse=True)
        return sorted_list

    def search_by_name(self, item_name: str) -> list:
        '''A func that was given by the teacher
        - fist make a list of all of the items that:
        ----- in the store but not in the cart
        ----- and for each one of them check if the name of the item that was given in the method is substring of the items in the store
        - then send all the items that in the cart and get a list of all the tags in the list (by using get_flat_tags)
        - then send the items to the sort func
        - finally return the sorted list '''
        search_items_by_name = [item for item in self._items if item not in self._shopping_cart.items_In_Cart and item_name in item.name]
        answer = self.sort_list(search_items_by_name)
        return answer
    
    def search_by_hashtag(self, hashtag: str) -> list:
        '''A func that was given by the teacher
        - fist make a list of all of the items that in store, if he have at list one common tag with the hashtag that was given
        - then make a list of all of the items that have at list one common tag with the hashtag that was given and not in cart
        - then send all the items that in the cart and get a list of all the tags in the list (by using get_flat_tags)
        - then send the items to the sort func
        - finally return the sorted list '''
        list_items_in_store = [item for item in self._items for tag in item.hashtags if tag == hashtag]
        list_items_in_store_and_not_in_cart = [item for item in list_items_in_store if item not in self._shopping_cart.items_In_Cart]
        answer = self.sort_list(list_items_in_store_and_not_in_cart)
        return answer


    def add_item(self, item_name: str):
        ''' first make a list of all the items in store that have the same name (or sub name)
        - if the list empty return exception
        - if there is more then one item in the list return exception
        - else send the first item (func add in shopping cart expect to get item and not list this why we use [0] ) to the shopping cart add func '''
        item = [item for item in self._items if item_name in item.name]
        if len(item) == 0:
            raise errors.ItemNotExistError
        if len(item) > 1:
            raise errors.TooManyMatchesError
        else:
            self._shopping_cart.add_item(item[0])

    def remove_item(self, item_name: str):
        ''' first make a list of all the items in store that have the same name (or sub name)
        - if the list empty return exception
        - if there is more then one item in the list return exception
        - else send the item name (func add in shopping cart expect to get item's name and not list this why we use [0].name) to the shopping cart remove func '''
        item = [item for item in self._shopping_cart.items_In_Cart if item_name in item.name]
        if len(item) == 0:
            raise errors.ItemNotExistError
        if len(item) > 1:
            raise errors.TooManyMatchesError
        else:
            self._shopping_cart.remove_item(item[0].name)

    def checkout(self) -> int:
        '''send to get_subtotal func in shopping cart to calculate the checkout'''
        total = self._shopping_cart.get_subtotal()
        return total
