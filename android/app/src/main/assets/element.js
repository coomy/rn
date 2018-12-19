
/**
 * Virtual-dom Element.
 * @param {String} tagName
 * @param {Object} props - Element's properties,
 *                       - using object to store key-value pair
 * @param {Array<Element|String>} - This element's childrens elements.
 *                                - Can be Element instance or just a piece plain text.
 */
function Element(tagName, props, childrens) {
  if (!(this instanceof Element)) {
    if (!_.isArray(childrens) && childrens != null) {
      childrens = _.slice(arguments, 2).filter(_.truthy)
    }
    return new Element(tagName, props, childrens)
  }

  if (_.isArray(props)) {
    childrens = props
    props = {}
  }

  this.tagName = tagName
  this.props = props || {}
  this.childrens = childrens || []
  this.key = props
    ? props.key
    : void 666

  var count = 0

  _.each(this.childrens, function(child, i) {
    if (child instanceof Element) {
      count += child.count
    } else {
      childrens[i] = '' + child
    }
    count++
  })

  this.count = count
}

/**
 * Render the hold element tree.
 */
Element.prototype.render = function() {
  var el = document.createElement(this.tagName)
  var props = this.props

  for (var propName in props) {
    var propValue = props[propName]
    _.setAttr(el, propName, propValue)
  }

  _.each(this.childrens, function(child) {
    var childEl = (child instanceof Element)
      ? child.render()
      : document.createTextNode(child)
    el.appendChild(childEl)
  })

  return el
}

Element.serialize = function(el) {
  return JSON.stringify(el)
}

Element.deserialize = function(json) {
  var el = JSON.parse(json)
  function makeElement(obj) {
    obj.__proto__ = Element.prototype
    // obj.constructor = Element
    _.each(obj.childrens, makeElement)
  }
  makeElement(el)
  return el
}

// module.exports = Element