import android.icu.text.DecimalFormat
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviemate2.Adapters.DateAdapter
import com.example.moviemate2.Adapters.SeatListAdapter
import com.example.moviemate2.Adapters.TimeAdapter
import com.example.moviemate2.databinding.ActivitySeatListBinding
import com.example.moviemate2.models.Seat
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SeatListActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var binding: ActivitySeatListBinding
    private var price: Double = 0.0
    private var number: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Razorpay
        Checkout.preload(applicationContext)

        setVariable()
        initSeatsList()

        // Set full-screen layout
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        // Set up Pay Now button click
        binding.button.setOnClickListener {
            if (number > 0) {
                startPayment()
            } else {
                Toast.makeText(this, "Please select seats to proceed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initSeatsList() {
        val gridLayoutManager = GridLayoutManager(this, 7)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = 1
        }

        binding.seatRecyclerview.layoutManager = gridLayoutManager
        val seatList = generateSeatList()
        val seatAdapter = SeatListAdapter(seatList, this, object : SeatListAdapter.SelectedSeat {
            override fun Return(selectedName: String, num: Int) {
                binding.numberSelectedTxt.text = "$num Seat(s) Selected"
                val df = DecimalFormat("#.##")
                price = df.format(num * 120.0).toDouble()  // Assuming price per seat is 120
                number = num
                binding.priceTxt.text = "Rs. $price"
            }
        })
        binding.seatRecyclerview.adapter = seatAdapter
        binding.seatRecyclerview.isNestedScrollingEnabled = false

        binding.TimeRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.TimeRecyclerview.adapter = TimeAdapter(generateTimeSlots())

        binding.dateRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.dateRecyclerview.adapter = DateAdapter(generateDates())
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID("YOUR_RAZORPAY_KEY_ID")  // Replace with your Razorpay API key

        // Amount in paise, so multiply the price by 100
        val amountInPaise = (price * 100).toInt()

        try {
            val options = JSONObject()
            options.put("name", "Movie Ticket Booking")
            options.put("description", "Payment for $number seats")
            options.put("currency", "INR")
            options.put("amount", amountInPaise)
            options.put("prefill.email", "paramaniksubrat0@gmail.com")  // Prefill with user details
            options.put("prefill.contact", "7846816120")

            checkout.open(this, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show()
        // Additional logic for successful payment can go here
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment failed: $response", Toast.LENGTH_SHORT).show()
        // Additional logic for payment failure can go here
    }

    private fun generateSeatList(): List<Seat> {
        val seatList = mutableListOf<Seat>()
        val numberSeats = 81

        for (i in 0 until numberSeats) {
            val seatStatus = if (i == 2 || i == 20 || i == 33 || i == 41 || i == 50 || i == 72 || i == 73)
                Seat.SeatStatus.UNAVAILABLE else Seat.SeatStatus.AVAILABLE
            seatList.add(Seat(seatStatus, "Seat ${i + 1}"))
        }
        return seatList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        for (i in 0 until 24 step 2) {
            val time = LocalTime.of(i, 0)
            timeSlots.add(time.format(formatter))
        }
        return timeSlots
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateDates(): List<String> {
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE/dd/MMM")
        for (i in 0 until 7) {
            dates.add(today.plusDays(i.toLong()).format(formatter))
        }
        return dates
    }
}
