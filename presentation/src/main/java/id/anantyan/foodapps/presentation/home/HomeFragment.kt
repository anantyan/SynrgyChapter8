package id.anantyan.foodapps.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import id.anantyan.foodapps.common.UIState
import id.anantyan.foodapps.common.calculateSpanCount
import id.anantyan.foodapps.presentation.databinding.FragmentHomeBinding
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    @Inject lateinit var adapter: HomeAdapter

    @Inject lateinit var adapterCategories: HomeCategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback())
        bindObserver()
        bindView()
    }

    private fun bindView() {
        binding.rvHome.setHasFixedSize(false)
        binding.rvHome.layoutManager = StaggeredGridLayoutManager(
            requireActivity().windowManager.calculateSpanCount(),
            RecyclerView.VERTICAL
        )
        binding.rvHome.itemAnimator = DefaultItemAnimator()
        binding.rvHome.isNestedScrollingEnabled = true
        binding.rvHome.adapter = adapter

        binding.rvType.setHasFixedSize(false)
        binding.rvType.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.HORIZONTAL,
            false
        )
        binding.rvType.itemAnimator = DefaultItemAnimator()
        binding.rvType.isNestedScrollingEnabled = true
        binding.rvType.adapter = adapterCategories

        adapter.stateRestorationPolicy = RecyclerView
            .Adapter
            .StateRestorationPolicy
            .PREVENT_WHEN_EMPTY
        adapter.onClick { _, item ->
            val destination = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                item.id ?: -1
            )
            findNavController().navigate(destination)
        }

        adapterCategories.stateRestorationPolicy = RecyclerView
            .Adapter
            .StateRestorationPolicy
            .PREVENT_WHEN_EMPTY
        adapterCategories.onClick { _, item ->
            viewModel.results(item.key)
        }
    }

    private fun bindObserver() {
        viewModel.resultsCategories.onEach { results ->
            adapterCategories.submitList(results)
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        viewModel.results.onEach { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.imgViewFavorite.isVisible = false
                    binding.rvHome.isVisible = false
                }
                is UIState.Success -> {
                    binding.progressBar.isVisible = false
                    adapter.submitList(state.data)
                    if (state.data?.isEmpty() == true) {
                        binding.imgViewFavorite.isVisible = true
                        binding.rvHome.isVisible = false
                    } else {
                        binding.imgViewFavorite.isVisible = false
                        binding.rvHome.isVisible = true
                    }
                }
                is UIState.Error -> {
                    binding.progressBar.isVisible = false
                    binding.imgViewFavorite.isVisible = true
                    binding.rvHome.isVisible = false
                }
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        viewModel.results()
    }

    private fun onBackPressedCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
