package ikhwan.binar.binarchallengelima.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import ikhwan.binar.binarchallengelima.R
import ikhwan.binar.binarchallengelima.adapter.MovieAdapter
import ikhwan.binar.binarchallengelima.database.UserDatabase
import ikhwan.binar.binarchallengelima.databinding.FragmentHomeBinding
import ikhwan.binar.binarchallengelima.model.popularmovie.ResultMovie
import ikhwan.binar.binarchallengelima.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private var userDatabase : UserDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Welcome, Ikhwan!"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        userDatabase = UserDatabase.getInstance(requireContext())
        viewModel.setUserDb(userDatabase!!)
        sharedPreferences = requireActivity().getSharedPreferences(PREF_USER, Context.MODE_PRIVATE)
        val email = sharedPreferences.getString(EMAIL, "").toString()

        viewModel.getUser(email)
        viewModel.fetchData()

        viewModel.user.observe(viewLifecycleOwner){
            (activity as AppCompatActivity?)!!.supportActionBar?.title = "Welcome, ${it.nama}!"
        }

        viewModel.listData.observe(viewLifecycleOwner){
            showList(it)
        }
        viewModel.isSuccess.observe(viewLifecycleOwner){
            if (it == true){
                binding.progressCircular.visibility = View.INVISIBLE
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_profileFragment)
                true
            }
            else -> true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    private fun showList(data: List<ResultMovie>?) {
        binding.rvMovie.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = MovieAdapter(object : MovieAdapter.OnClickListener{
            override fun onClickItem(data: ResultMovie) {
                val mBundle = bundleOf(DetailFragment.EXTRA_DATA to data)
                Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_detailFragment, mBundle)
            }
        })
        adapter.submitData(data)
        binding.rvMovie.adapter = adapter
    }

    companion object {
        const val PREF_USER = "user_preference"
        const val EMAIL = "email"
    }

}