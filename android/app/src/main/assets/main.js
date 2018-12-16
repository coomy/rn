function renderTree () {
  var items = []
  for (var i = 0; i < 5; i++) {
    items.push(Element('li', ['Item #' + i]))
  }

  return Element('div', {'id': 'container'}, [
    Element('h1', {style: 'color: red'}, ['simple virtal dom']),
    Element('p', ['hello world']),
    Element('ul', items)
  ])
}

var tree = renderTree()
Element.serialize(tree)