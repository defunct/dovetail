/**
 * <p>Dovetail is a library for matching URLs and extracting their
 *  component parts.</p>
 * <p>
 * Thu May 20 23:33:00 CDT 2010
 * <p>
 * Dovetail was written back in the day when I was favoring immutability, so
 * obviously I had to build immmutable objects from object builders. That is why
 * this simply pattern matching engine is weight in at 55k, the same size the
 * dependency injection engine I recently wrote, Ilk Inject.
 * <p>
 * Although, I'm not too concerned. There is not much to compete with Dovetail. I
 * don't know of another tool that maps paths to objects in this fashion.
 * <p>
 * I'm going to make this an exericise in honoring the intent of the developer I
 * was two years ago, preserving the API I concieved. Of course, the uncompromising
 * nature of that development means there are some dead ends that need to be
 * pruned.
 */
package com.goodworkalan.dovetail;

