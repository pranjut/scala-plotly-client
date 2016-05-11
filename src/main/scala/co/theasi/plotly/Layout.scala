package co.theasi.plotly

// /** Base class for plot layouts.
//   *
//   * Do not construct this class directly. Use one of the sub-classes instead:
//   *
//   *  - [[SingleAxisLayout]] to have a single pair of axes on the figure
//   *  - [[RowLayout]] for subplots equally distributed in a single line
//   *  - [[GridLayout]] for subplots distributed on a grid.
//   */
// sealed trait Layout[A <: Layout[A]] {
//   val options: LayoutOptions

//   def newOptions(newOptions: LayoutOptions): A

// }

// /** Layout with a single plot.
//   *
//   * ==Example usage==
//   *
//   * {{{
//   * val xs = Vector(1, 2, 3)
//   * val ys = Vector(4.5, 8.5, 21.0)
//   *
//   * val layout = SingleAxisLayout()
//   *   .leftMargin(140)
//   *   .paperBackgroundColor(254, 247, 234)
//   *   .plotBackgroundColor(254, 247, 234)
//   *   .xAxisOptions(AxisOptions()
//   *     .titleColor(204, 204, 204)
//   *     .noAutoTick)
//   *
//   * val plot = Plot().layout(layout).withScatter(xs, ys)
//   * }}}
//   */
// case class SingleAxisLayout(
//     content: Subplot,
//     options: LayoutOptions)
// extends Layout[SingleAxisLayout] {

//   def newOptions(newOptions: LayoutOptions): SingleAxisLayout =
//     copy(options = newOptions)

// }


// object SingleAxisLayout {
//   def apply(): SingleAxisLayout =
//     SingleAxisLayout(this.CartesianSubplot(), LayoutOptions())
// }


// /** Layout with a grid of sub-plots.
//   *
//   * This creates a grid of subplots arranged in rows and columns.
//   *
//   * ==Example Usage==
//   *
//   * Let's draw six scatter plots on a grid.
//   *
//   * {{{
//   * import util.Random
//   *
//   * val xs = (0 to 100).map { i => Random.nextGaussian }
//   * def ys = (0 to 100).map { i => Random.nextGaussian }
//   *
//   * // 2 rows, 3 columns
//   * val layout = GridLayout(2, 3)
//   * }}}
//   *
//   * To create a new plot with this layout, we use the [[Plot.layout]] method:
//   *
//   * {{{
//   * val p = Plot().layout(layout)
//   * }}}
//   *
//   * We can specify which subplot a new data series will be plotted to using
//   * the 'onAxes' series option.
//   *
//   * {{{
//   * val p = Plot()
//   *   .layout(layout)
//   *   .withScatter(xs, ys, ScatterOptions().onAxes(layout.ref(0, 1))) // top middle
//   * }}}
//   *
//   * The `.ref` method takes a (row, column) pair as arguments and returns a single
//   * axis index that can be passed to `.onAxes`.
//   *
//   */
// case class GridLayout(
//     xAxes: Vector[Axis],
//     yAxes: Vector[Axis],
//     subplots: Vector[Subplot],
//     numberRows: Int,
//     numberColumns: Int,
//     options: LayoutOptions
// ) extends Layout[GridLayout] {
//
//   //def xAxis(row: Int, column: Int) = xAxes(subplot(row, column).xAxisRef)
//   //def yAxis(row: Int, column: Int) = yAxes(subplot(row, column).yAxisRef)
//
//   /** Set new axis options for the x-axis of one of the subplots. */
//   def xAxisOptions(row: Int, column: Int, newOptions: AxisOptions)
//   : GridLayout = {
//     val axisRef = subplot(row, column) match {
//       case s: CartesianSubplot => s.xAxisRef
//       case _ => throw new IllegalArgumentException(
//         s"Subplot at ($row, $column) is not Cartesian")
//     }
//     val newAxis = xAxes(axisRef).copy(options = newOptions)
//     copy(xAxes = xAxes.updated(axisRef, newAxis))
//   }
//
//   /** Set new axis options for the y-axis of one of the subplots. */
//   def yAxisOptions(row: Int, column: Int, newOptions: AxisOptions) = {
//     val axisRef = subplot(row, column) match {
//       case s: CartesianSubplot => s.yAxisRef
//       case _ => throw new IllegalArgumentException(
//         s"Subplot at ($row, $column) is not Cartesian")
//     }
//     val newAxis = yAxes(axisRef).copy(options = newOptions)
//     copy(yAxes = yAxes.updated(axisRef, newAxis))
//   }
//
//   /** Returns the index of axes at (row, column)
//     *
//     * The main use case for this is to generate an argument
//     * for the `.onAxes` method, to specify which sub-plot a
//     * new series should be drawn on.
//     *
//     * @example {{{
//     * // grid layout with 2 rows and 3 columns
//     * val layout = GridLayout(2, 3)
//     *
//     * // options for plotting on subplot (0, 1): top row, middle column
//     * val options = ScatterOptions().onAxes(layout.ref(0, 1))
//     * }}}
//     */
//   def subplot(row: Int, column: Int): Subplot = {
//     checkRowColumn(row, column)
//     subplots(rowColumnToRefImpl(row, column))
//   }
//
//   def newOptions(newOptions: LayoutOptions): GridLayout =
//     copy(options = newOptions)
//
//   private def rowColumnToRefImpl(row: Int, column: Int): Int = {
//     val value = row*numberColumns + column
//     value
//   }
//
//   private def checkRowColumn(row: Int, column: Int) {
//     checkRow(row)
//     checkColumn(column)
//   }
//
//   private def checkRow(row: Int) {
//     if (row >= numberRows) {
//       throw new IllegalArgumentException(s"Row index $row out of bounds.")
//     }
//   }
//
//   private def checkColumn(column: Int) {
//     if (column >= numberColumns) {
//       throw new IllegalArgumentException(s"Column index $column out of bounds.")
//     }
//   }
//
// }
//
//
// object GridLayout {
//
//   val DefaultHorizontalSpacing = 0.2
//   val DefaultVerticalSpacing = 0.3
//
//   def apply(numberRows: Int, numberColumns: Int): GridLayout = {
//
//     // Spacing between plots
//     val horizontalSpacing = DefaultHorizontalSpacing / numberColumns.toDouble
//     val verticalSpacing = DefaultVerticalSpacing / numberRows.toDouble
//
//     // plot width
//     val width =
//       (1.0 - horizontalSpacing * (numberColumns - 1))/numberColumns.toDouble
//
//     // plot height
//     val height =
//       (1.0 - verticalSpacing * (numberRows - 1))/numberRows.toDouble
//
//     val xDomains = (0 until numberColumns).map { icol =>
//       val start = icol * (width + horizontalSpacing)
//       val end = start + width
//       (start, end)
//     }
//
//     val yDomains = (0 until numberRows).map { irow =>
//       val top = 1.0 - (irow * (height + verticalSpacing))
//       val bottom = top - height
//       (bottom, top)
//     }
//
//     // Cartesian product of xDomains and yDomains
//     val viewPorts = for {
//       yDomain <- yDomains
//       xDomain <- xDomains
//     } yield ViewPort(xDomain, yDomain)
//
//     val subPlots = viewPorts.zipWithIndex.map {
//       case (viewPort, index) => CartesianSubplot(viewPort, index, index)
//     }.toVector
//
//     val xAxes = Vector.fill(subPlots.size)(Axis())
//     val yAxes = Vector.fill(subPlots.size)(Axis())
//
//     GridLayout(xAxes, yAxes, subPlots,
//       numberRows, numberColumns, LayoutOptions())
//   }
// }
//
//
// /** Layout with a row of subplots.
//   *
//   * ==Example Usage==
//   *
//   * Let's draw 2 scatter plots.
//   * {{{
//   * import util.Random
//   *
//   * val xsLeft = (0 to 100).map { i => Random.nextGaussian }
//   * val ysLeft = (0 to 100).map { i => Random.nextGaussian }
//   *
//   * val xsRight = (0 to 100).map { i => Random.nextDouble }
//   * val ysRight = (0 to 100).map { i => Random.nextDouble }
//   *
//   * // 2 subplots
//   * val layout = RowLayout(2)
//   * }}}
//   *
//   * To create a new plot with this layout, we use the [[Plot.layout]] method:
//   *
//   * {{{
//   * val p = Plot().layout(layout)
//   * }}}
//   * We can specify which subplot a new data series will be plotted to using the 'onAxes'
//   * series option.
//   *
//   * {{{
//   * val p = Plot().layout(layout)
//   *   .withScatter(xsLeft, ysLeft,
//   *     ScatterOptions()
//   *       .onAxes(layout.ref(0)) // left subplot
//   *       .mode(ScatterMode.Marker))
//   *   .withScatter(xsRight, ysRight,
//   *     ScatterOptions()
//   *       .onAxes(layout.ref(1)) // right subplot
//   *       .mode(ScatterMode.Marker))
//   * }}}
//   *
//   * The `.ref` method takes a single index denoting which subplot to plot on, and
//   * returns an index that can be passed to `.onAxes`.
//   */
// case class RowLayout(private val impl: GridLayout)
// extends Layout[RowLayout] {
//   // The implementation is just a wrapper around a GridLayout with
//   // a single row.
//   def xAxes = impl.xAxes
//   def yAxes = impl.yAxes
//   def subplots = impl.subplots
//   val options = impl.options
//
//   /** Returns the index of the axes on a subplot
//     *
//     * The main use case for this is to generate an argument
//     * for the `.onAxes` method, to specify which sub-plot a
//     * new series should be drawn on.
//     *
//     * @example {{{
//     * // Layout with 3 subplots
//     * val layout = RowLayout(3)
//     *
//     * // options for plotting on subplot (1): middle subplot
//     * val options = ScatterOptions().onAxes(layout.ref(1))
//     * }}}
//     */
//   def subplot(position: Int): Subplot = impl.subplot(0, position)
//
//   /** Set new axis options for the x-axis of one of the subplots. */
//   def xAxisOptions(subplot: Int, newOptions: AxisOptions): RowLayout =
//     copy(impl.xAxisOptions(0, subplot, newOptions))
//
//   /** Set new axis options for the y-axis of one of the subplots. */
//   def yAxisOptions(subplot: Int, newOptions: AxisOptions): RowLayout =
//     copy(impl.yAxisOptions(0, subplot, newOptions))
//
//   def newOptions(newOptions: LayoutOptions): RowLayout =
//     copy(impl.newOptions(newOptions))
//
// }
//
//
// object RowLayout {
//   def apply(numberPlots: Int): RowLayout =
//     RowLayout(GridLayout(1, numberPlots))
// }
//
// // case class FlexibleLayout(xAxes: Vector[Axis], yAxes: Vector[Axis])
// // extends Layout[FlexibleLayout] {}
