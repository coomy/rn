var el = require('./element')

var ul = el('ul', {id: 'list'}, [
  el('li', {class: 'item'}, ['item 1']),
  el('li', {class: 'item'}, ['item 2']),
  el('li', {class: 'item'}, ['item 3'])
])