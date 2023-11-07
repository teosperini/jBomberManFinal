package org.jbomberman.updatemanager;

import org.jbomberman.utils.Coordinate;

public record UMovement(Coordinate oldC, Coordinate newC, int id) {
}
