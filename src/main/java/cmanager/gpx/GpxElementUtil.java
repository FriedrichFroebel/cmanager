package cmanager.gpx;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheAttribute;
import cmanager.geo.GeocacheLog;
import cmanager.geo.Waypoint;
import cmanager.gui.ExceptionPanel;
import cmanager.xml.Element;
import cmanager.xml.XmlAttribute;
import java.util.ArrayList;
import java.util.List;

public class GpxElementUtil {

    static Waypoint xmlToWaypoint(Element waypointElement) {
        Coordinate coordinate;
        String code = null;
        String description = null;
        String symbol = null;
        String type = null;
        String parent = null;
        String date = null;

        double latitude = 0.0;
        double longitude = 0.0;
        for (final XmlAttribute attribute : waypointElement.getAttributes()) {
            if (attribute.is("lat")) {
                latitude = attribute.getValueDouble();
            } else if (attribute.is("lon")) {
                longitude = attribute.getValueDouble();
            }
        }

        coordinate = new Coordinate(latitude, longitude);

        for (final Element element : waypointElement.getChildren()) {
            if (element.is("name")) {
                code = element.getUnescapedBody();
            } else if (element.is("desc")) {
                description = element.getUnescapedBody();
            } else if (element.is("sym")) {
                symbol = element.getUnescapedBody();
            } else if (element.is("type")) {
                type = element.getUnescapedBody();
            } else if (element.is("time")) {
                date = element.getUnescapedBody();
            } else if (element.is("gsak:wptExtension")) {
                for (final Element extensionElement : element.getChildren()) {
                    if (extensionElement.is("gsak:Parent")) {
                        parent = extensionElement.getUnescapedBody();
                    }
                }
            }
        }

        final Waypoint waypoint = new Waypoint(coordinate, code, description, symbol, type, parent);
        waypoint.setDate(date);
        return waypoint;
    }

    static Geocache xmlToCache(Element waypointElement) {
        String code = null;
        String urlName = null;
        String cacheName = null;
        Coordinate coordinate;
        Double difficulty = null;
        Double terrain = null;
        String type = null;
        String owner = null;
        String container = null;
        String listing = null;
        String listingShort = null;
        String hint = null;
        Integer id = null;
        Boolean archived = null;
        Boolean available = null;
        Boolean gcPremium = null;
        Integer favoritePoints = null;

        final List<GeocacheAttribute> attributes = new ArrayList<>();
        final List<GeocacheLog> logs = new ArrayList<>();

        double latitude = 0.0;
        double longitude = 0.0;
        for (final XmlAttribute attribute : waypointElement.getAttributes()) {
            if (attribute.is("lat")) {
                latitude = attribute.getValueDouble();
            } else if (attribute.is("lon")) {
                longitude = attribute.getValueDouble();
            }
        }
        coordinate = new Coordinate(latitude, longitude);

        boolean groundspeak_cache = false;
        for (final Element element : waypointElement.getChildren()) {
            if (element.is("name")) {
                code = element.getUnescapedBody();
            } else if (element.is("urlname")) {
                urlName = element.getUnescapedBody();
            } else if (element.is("groundspeak:cache")) {
                groundspeak_cache = true;

                for (final XmlAttribute attribute : element.getAttributes()) {
                    if (attribute.is("id")) {
                        try {
                            id = Integer.valueOf(attribute.getValue());
                        } catch (Exception ignored) {
                        }
                    }
                    if (attribute.is("archived")) {
                        archived = Boolean.valueOf(attribute.getValue());
                    } else if (attribute.is("available")) {
                        available = Boolean.valueOf(attribute.getValue());
                    }
                }

                for (final Element groundspeakElement : element.getChildren()) {
                    if (groundspeakElement.is("groundspeak:name")) {
                        cacheName = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:difficulty")) {
                        difficulty = groundspeakElement.getBodyDouble();
                    } else if (groundspeakElement.is("groundspeak:terrain")) {
                        terrain = groundspeakElement.getBodyDouble();
                    } else if (groundspeakElement.is("groundspeak:type")) {
                        type = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:owner")) {
                        owner = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:container")) {
                        container = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:long_description")) {
                        listing = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:short_description")) {
                        listingShort = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:encoded_hints")) {
                        hint = groundspeakElement.getUnescapedBody();
                    } else if (groundspeakElement.is("groundspeak:logs")) {
                        for (final Element logElement : groundspeakElement.getChildren())
                            if (logElement.is("groundspeak:log")) {
                                // Skip geotoad info log.
                                if (logElement.attrIs("id", "-2")) {
                                    continue;
                                }

                                String logType = null;
                                String author = null;
                                String text = null;
                                String date = null;

                                for (final Element logChildren : logElement.getChildren()) {
                                    if (logChildren.is("groundspeak:date")) {
                                        date = logChildren.getUnescapedBody();
                                    } else if (logChildren.is("groundspeak:type")) {
                                        logType = logChildren.getUnescapedBody();
                                    } else if (logChildren.is("groundspeak:finder")) {
                                        author = logChildren.getUnescapedBody();
                                    } else if (logChildren.is("groundspeak:text")) {
                                        text = logChildren.getUnescapedBody();
                                    }
                                }

                                if (logType != null && logType.equals("Other")) {
                                    continue;
                                }

                                try {
                                    final GeocacheLog log =
                                            new GeocacheLog(logType, author, text, date);
                                    logs.add(log);
                                } catch (NullPointerException | IllegalArgumentException ex) {
                                    ExceptionPanel.display(ex);
                                }
                            }
                    } else if (groundspeakElement.is("groundspeak:attributes")) {
                        for (final Element attributeElement : groundspeakElement.getChildren()) {
                            if (attributeElement.is("groundspeak:attribute")) {
                                Integer attributeId = null;
                                Integer attributeInc = null;

                                for (final XmlAttribute attributeAttribute :
                                        attributeElement.getAttributes()) {
                                    if (attributeAttribute.is("id")) {
                                        attributeId = attributeAttribute.getValueInteger();
                                    } else if (attributeAttribute.is("inc")) {
                                        attributeInc = attributeAttribute.getValueInteger();
                                    }
                                }

                                final String description = attributeElement.getUnescapedBody();

                                try {
                                    final GeocacheAttribute attribute =
                                            new GeocacheAttribute(
                                                    attributeId, attributeInc, description);
                                    attributes.add(attribute);
                                } catch (NullPointerException
                                        | IllegalArgumentException exception) {
                                    ExceptionPanel.display(exception);
                                }
                            }
                        }
                    }
                }
            } else if (element.is("gsak:wptExtension")) {
                for (final Element extensionElement : element.getChildren()) {
                    if (extensionElement.is("gsak:IsPremium")) {
                        gcPremium = extensionElement.getBodyBoolean();
                    } else if (extensionElement.is("gsak:FavPoints")) {
                        favoritePoints = extensionElement.getBodyInteger();
                    }
                }
            }
        }

        if (!groundspeak_cache) {
            return null;
        }

        if (container != null && container.equals("unknown")) {
            container = null;
        }

        final Geocache geocache =
                new Geocache(
                        code,
                        cacheName != null ? cacheName : urlName,
                        coordinate,
                        difficulty,
                        terrain,
                        type);
        geocache.setOwner(owner);
        geocache.setContainer(container);
        geocache.setListing(listing);
        geocache.setListingShort(listingShort);
        geocache.setHint(hint);
        geocache.setId(id);
        geocache.setArchived(archived);
        geocache.setAvailable(available);
        geocache.setGcPremium(gcPremium);
        geocache.setFavoritePoints(favoritePoints);
        geocache.addAttributes(attributes);
        geocache.addLogs(logs);

        return geocache;
    }

    static Element waypointToXml(Waypoint waypoint) {
        final Element waypointElement = new Element("wpt");
        waypointElement.add(new XmlAttribute("lat", waypoint.getCoordinate().getLatitude()));
        waypointElement.add(new XmlAttribute("lon", waypoint.getCoordinate().getLongitude()));

        waypointElement.add(new Element("time", waypoint.getDateStrIso8601()));
        waypointElement.add(new Element("name", waypoint.getCode()));
        waypointElement.add(new Element("desc", waypoint.getDescription()));
        waypointElement.add(new Element("sym", waypoint.getSymbol()));
        waypointElement.add(new Element("type", waypoint.getType()));

        final Element gsakExtension = new Element("gsak:wptExtension");
        waypointElement.add(gsakExtension);
        gsakExtension.add(new Element("gsak:Parent", waypoint.getParent()));

        return waypointElement;
    }

    static Element cacheToXml(Geocache geocache) {
        final Element waypoint = new Element("wpt");
        waypoint.add(new XmlAttribute("lat", geocache.getCoordinate().getLatitude()));
        waypoint.add(new XmlAttribute("lon", geocache.getCoordinate().getLongitude()));

        waypoint.add(new Element("name", geocache.getCode()));
        waypoint.add(new Element("urlname", geocache.getName()));

        final Element groundspeakCache = new Element("groundspeak:cache");
        groundspeakCache.add(new XmlAttribute("id", geocache.getId()));
        groundspeakCache.add(new XmlAttribute("available", geocache.isAvailable()));
        groundspeakCache.add(new XmlAttribute("archived", geocache.isArchived()));
        waypoint.add(groundspeakCache);

        final Element groundspeakAttributes = new Element("groundspeak:attributes");
        groundspeakCache.add(groundspeakAttributes);
        for (GeocacheAttribute attribute : geocache.getAttributes()) {
            final Element groundspeakAttribute = new Element("groundspeak:attribute");
            groundspeakAttribute.add(new XmlAttribute("id", attribute.getId()));
            groundspeakAttribute.add(new XmlAttribute("inc", attribute.getInc()));
            groundspeakAttribute.setBody(attribute.getDescription());
            groundspeakAttributes.add(groundspeakAttribute);
        }

        groundspeakCache.add(new Element("groundspeak:name", geocache.getName()));
        groundspeakCache.add(new Element("groundspeak:difficulty", geocache.getDifficulty()));
        groundspeakCache.add(new Element("groundspeak:terrain", geocache.getTerrain()));
        groundspeakCache.add(new Element("groundspeak:type", geocache.getType().asGcType()));
        groundspeakCache.add(new Element("groundspeak:owner", geocache.getOwner()));
        groundspeakCache.add(new Element("groundspeak:container", geocache.getContainer().asGc()));
        groundspeakCache.add(new Element("groundspeak:long_description", geocache.getListing()));
        groundspeakCache.add(
                new Element("groundspeak:short_description", geocache.getListingShort()));
        groundspeakCache.add(new Element("groundspeak:encoded_hints", geocache.getHint()));

        if (geocache.getLogs().size() > 0) {
            final Element groundspeakLogs = new Element("groundspeak:logs");
            groundspeakCache.add(groundspeakLogs);

            for (final GeocacheLog log : geocache.getLogs()) {
                final Element groundspeakLog = new Element("groundspeak:log");
                groundspeakLogs.add(groundspeakLog);

                groundspeakLog.add(new Element("groundspeak:date", log.getDateStrIso8601()));
                groundspeakLog.add(new Element("groundspeak:type", log.getTypeStr()));
                groundspeakLog.add(new Element("groundspeak:finder", log.getAuthor()));
                groundspeakLog.add(new Element("groundspeak:text", log.getText()));
            }
        }

        final Element gsakExtension = new Element("gsak:wptExtension");
        gsakExtension.add(new Element("gsak:IsPremium", geocache.isGcPremium()));
        gsakExtension.add(new Element("gsak:FavPoints", geocache.getFavoritePoints()));
        waypoint.add(gsakExtension);

        return waypoint;
    }
}
